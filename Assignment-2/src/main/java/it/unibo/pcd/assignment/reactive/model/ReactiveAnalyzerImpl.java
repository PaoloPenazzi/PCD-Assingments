package it.unibo.pcd.assignment.reactive.model;

import com.github.javaparser.ParseResult;
import com.github.javaparser.ParserConfiguration;
import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.PackageDeclaration;
import com.github.javaparser.ast.body.BodyDeclaration;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.TypeDeclaration;
import com.github.javaparser.utils.SourceRoot;
import io.reactivex.rxjava3.core.Maybe;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import it.unibo.pcd.assignment.event.collector.ClassCollector;
import it.unibo.pcd.assignment.event.collector.InterfaceCollector;
import it.unibo.pcd.assignment.event.collector.PackageCollector;
import it.unibo.pcd.assignment.event.collector.ProjectCollector;
import it.unibo.pcd.assignment.event.report.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.nio.file.Paths;
import java.util.List;

public class ReactiveAnalyzerImpl implements ReactiveAnalyzer {
    private String analysisPath;
    private String analysisType;

    @Override
    public Observable<String> analyzeProject(String srcProjectFolderName) {
        return Observable.<PackageDeclaration>create(emitter -> {
                    SourceRoot sourceRoot = new SourceRoot(Paths.get(srcProjectFolderName)).setParserConfiguration(new ParserConfiguration());
                    List<ParseResult<CompilationUnit>> parseResultList = sourceRoot.tryToParseParallelized();

                    List<PackageDeclaration> allCus = parseResultList.stream()
                            .filter(r -> r.getResult().isPresent() && r.isSuccessful())
                            .map(r -> r.getResult().get())
                            .filter(c -> c.getPackageDeclaration().isPresent())
                            .map(c -> c.getPackageDeclaration().get()).distinct().toList();
                    allCus.forEach(emitter::onNext);
                })
                .subscribeOn(Schedulers.computation())
                .concatMap(packageDeclaration -> Observable.just(packageDeclaration)
                        .subscribeOn(Schedulers.computation())
                        .flatMap(p -> Observable.create(emitter -> {
                            PackageReportImpl packageNameReport = new PackageReportImpl();
                            packageNameReport.setFullPackageName(p.getNameAsString());
                            emitter.onNext(packageNameReport.toString());
                            String sourceRootPath = this.analysisPath;
                            List<CompilationUnit> classesOrInterfacesUnit = this.createParsedFileList(packageDeclaration, sourceRootPath)
                                    .stream()
                                    .filter(r -> r.isSuccessful() && r.getResult().isPresent())
                                    .map(r -> r.getResult().get()).toList();
                            for (CompilationUnit cu : classesOrInterfacesUnit) {
                                List<ClassOrInterfaceDeclaration> declarationList = cu.getTypes().stream()
                                        .map(TypeDeclaration::asTypeDeclaration)
                                        .filter(BodyDeclaration::isClassOrInterfaceDeclaration)
                                        .map(x -> (ClassOrInterfaceDeclaration) x).toList();

                                for (ClassOrInterfaceDeclaration declaration : declarationList) {
                                    if (isTheRightPackage(packageDeclaration.getNameAsString(), declaration)) {
                                        if (declaration.isInterface()) {
                                            this.getInterfaceReport(this.analysisPath + "/" + declaration.getFullyQualifiedName().get().replace(".", "/") + ".java")
                                                    .blockingSubscribe(report -> emitter.onNext(report.toString()));
                                        } else {
                                            this.getClassReport(this.analysisPath + "/" + declaration.getFullyQualifiedName().get().replace(".", "/") + ".java")
                                                    .blockingSubscribe(report -> emitter.onNext(report.toString()));
                                        }
                                    }
                                }
                            }
                            emitter.onComplete();
                        })));
    }

    private List<ParseResult<CompilationUnit>> createParsedFileList(PackageDeclaration packageDeclaration, String sourceRootPath) {
        SourceRoot sourceRoot = new SourceRoot(Paths.get(sourceRootPath)).setParserConfiguration(new ParserConfiguration());
        List<ParseResult<CompilationUnit>> parseResultList;
        parseResultList = sourceRoot.tryToParseParallelized(packageDeclaration.getNameAsString());
        return parseResultList;
    }

    @Override
    public Maybe<ProjectReport> getProjectReport(String projectPath) {
        return Maybe.create(emitter -> {
            ProjectReportImpl projectReport = new ProjectReportImpl();
            ProjectCollector projectCollector = new ProjectCollector();
            projectCollector.visit(projectPath, projectReport);
            emitter.onSuccess(projectReport);
        });
    }

    @Override
    public Maybe<PackageReport> getPackageReport(String packagePath) {
        return Maybe.create(emitter -> {
            String path = packagePath;
            String sourceRoot = "";
            if (path.contains("\\")) {
                path = path.replace("\\", "/");
            }
            if (packagePath.contains("src/main/java")) {
                path = packagePath.replaceAll(".*src/main/java/", "");
                path = path.replaceAll("/", ".");
                sourceRoot = packagePath.replaceAll("src/main/java.*", "");
                sourceRoot = sourceRoot + "src/main/java";
            } else {
                throw new IllegalStateException("Wrong Path");
            }
            PackageDeclaration packageDeclaration = StaticJavaParser.parsePackageDeclaration("package " + path + ";");
            PackageReportImpl packageReport = new PackageReportImpl();
            PackageCollector packageCollector = new PackageCollector();
            packageCollector.setPath(sourceRoot);
            packageCollector.visit(packageDeclaration, packageReport);
            emitter.onSuccess(packageReport);
        });
    }

    @Override
    public Maybe<ClassReport> getClassReport(String classPath) {
        return Maybe.create(emitter -> {
            CompilationUnit compilationUnit = this.parseSingleFile(classPath);
            ClassOrInterfaceDeclaration declaration = compilationUnit.getTypes().stream().map(TypeDeclaration::asTypeDeclaration)
                    .filter(BodyDeclaration::isClassOrInterfaceDeclaration)
                    .map(ClassOrInterfaceDeclaration.class::cast).findFirst().get();
            if (!declaration.isInterface()) {
                ClassReport classReport = this.createClassReport(compilationUnit
                        .getPackageDeclaration().get().getNameAsString(), declaration);
                emitter.onSuccess(classReport);
            }
        });
    }

    private ClassReport createClassReport(String packageName, ClassOrInterfaceDeclaration declaration) {
        if (this.isTheRightPackage(packageName, declaration)) {
            ClassReportImpl classReport = new ClassReportImpl();
            ClassCollector classCollector = new ClassCollector();
            classCollector.visit(declaration, classReport);
            return classReport;
        } else {
            return null;
        }
    }

    @Override
    public Maybe<InterfaceReport> getInterfaceReport(String interfacePath) {
        return Maybe.create(emitter -> {
            CompilationUnit compilationUnit = this.parseSingleFile(interfacePath);
            ClassOrInterfaceDeclaration declaration = compilationUnit.getTypes().stream().map(TypeDeclaration::asTypeDeclaration)
                    .filter(BodyDeclaration::isClassOrInterfaceDeclaration)
                    .map(ClassOrInterfaceDeclaration.class::cast).findFirst().get();
            if (declaration.isInterface()) {
                InterfaceReport interfaceReport = this.createInterfaceReport(compilationUnit
                        .getPackageDeclaration().get().getNameAsString(), declaration);
                emitter.onSuccess(interfaceReport);
            }
        });
    }

    private InterfaceReport createInterfaceReport(String packageName, ClassOrInterfaceDeclaration declaration) {
        if (this.isTheRightPackage(packageName, declaration)) {
            InterfaceReportImpl interfaceReport = new InterfaceReportImpl();
            InterfaceCollector interfaceCollector = new InterfaceCollector();
            interfaceCollector.visit(declaration, interfaceReport);
            return interfaceReport;
        } else {
            return null;
        }
    }

    private CompilationUnit parseSingleFile(String srcFilePath) {
        CompilationUnit compilationUnit;
        try {
            compilationUnit = StaticJavaParser.parse(new File(srcFilePath));
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
        return compilationUnit;
    }

    private boolean isTheRightPackage(String packageName, ClassOrInterfaceDeclaration declaration) {
        String classFullName = declaration.getFullyQualifiedName().get();
        String className = declaration.getNameAsString();
        classFullName = classFullName.replace("." + className, "");
        return classFullName.equals(packageName);
    }

    public String getAnalysisPath() {
        return this.analysisPath;
    }

    public void setAnalysisPath(String newPath) {
        this.analysisPath = newPath;
    }

    public String getAnalysisType() {
        return analysisType;
    }

    public void setAnalysisType(String analysisType) {
        this.analysisType = analysisType;
    }
}
