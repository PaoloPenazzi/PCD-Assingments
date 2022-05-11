package it.unibo.pcd.assignment.reactive.model;

import com.github.javaparser.ParseResult;
import com.github.javaparser.ParserConfiguration;
import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.PackageDeclaration;
import com.github.javaparser.ast.body.*;
import com.github.javaparser.utils.SourceRoot;
import io.reactivex.rxjava3.core.Maybe;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import io.reactivex.rxjava3.subjects.PublishSubject;
import io.reactivex.rxjava3.subjects.Subject;
import it.unibo.pcd.assignment.event.ProjectElem;
import it.unibo.pcd.assignment.event.collector.ClassCollector;
import it.unibo.pcd.assignment.event.collector.InterfaceCollector;
import it.unibo.pcd.assignment.event.collector.PackageCollector;
import it.unibo.pcd.assignment.event.collector.ProjectCollector;
import it.unibo.pcd.assignment.event.report.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

public class ReactiveAnalyzerImpl implements ReactiveAnalyzer {
    private final Subject<Integer> packageNumberObservable = PublishSubject.create();
    private final Subject<Integer> classNumberObservable = PublishSubject.create();
    private final Subject<Integer> interfaceNumberObservable = PublishSubject.create();
    private final Subject<ProjectElem> reportObservable = PublishSubject.create();
    private int packageNumber;
    private int classNumber;
    private int interfaceNumber;
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
                            //String packagePath = packageDeclaration.getNameAsString();
                            List<CompilationUnit> classesOrInterfacesUnit = this.createParsedFileList(packageDeclaration, sourceRootPath)
                                    .stream()
                                    .filter(r -> r.isSuccessful() && r.getResult().isPresent())
                                    .map(r -> r.getResult().get()).toList();
                            incrementPackageNumber(1);
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
            incrementPackageNumber(projectReport.getPackageReport().size());
            for (PackageReport packageReport : projectReport.getPackageReport()) {
                incrementInterfaceNumber(packageReport.getInterfacesReport().size());
                incrementClassNumber(packageReport.getClassesReport().size());
            }
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
            this.incrementPackageNumber(1);
            this.incrementClassNumber(packageReport.getClassesReport().size());
            this.incrementInterfaceNumber(packageReport.getInterfacesReport().size());
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
            incrementClassNumber(1);
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
            incrementInterfaceNumber(1);
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

    private void buildClassReport(ClassOrInterfaceDeclaration declaration) {
        /*String className = declaration.getFullyQualifiedName().get();
        String fieldsString = "";
        String methodsString = "";
        List<FieldDeclaration> fieldDeclarationList = declaration.getFields();
        for (FieldDeclaration fieldDeclaration : fieldDeclarationList) {
            fieldsString = fieldsString.concat("\t\t" + "FIELD:  " + fieldDeclaration.toString() + "\n");
        }
        List<MethodDeclaration> methodDeclarationList = declaration.getMethods();
        for (MethodDeclaration methodDeclaration : methodDeclarationList) {
            methodsString = methodsString.concat("\t\t" + "METHOD:  " + methodDeclaration.getDeclarationAsString(true, false, true) + "\n");
        }
        String report = "\t" + "CLASS:  " + className + "\n" + fieldsString + methodsString;
        addReport(report);*/
    }

    private void buildInterfaceReport(ClassOrInterfaceDeclaration declaration) {
        /*String interfaceName = declaration.getFullyQualifiedName().get();
        String methodsString = "";
        List<MethodDeclaration> methodDeclarationList = declaration.getMethods();
        for (MethodDeclaration methodDeclaration : methodDeclarationList) {
            methodsString = methodsString.concat("\t\t" + "METHOD:  " + methodDeclaration.getDeclarationAsString(true, false, true) + "\n");
        }
        String report = "\t" + "INTERFACE:  " + interfaceName + "\n" + methodsString;
        addReport(report);*/
    }

    public void incrementPackageNumber(int variation) {
        this.packageNumberObservable.onNext(this.packageNumber += variation);
    }

    public void incrementClassNumber(int variation) {
        this.classNumberObservable.onNext(this.classNumber += variation);
    }

    public void incrementInterfaceNumber(int variation) {
        this.interfaceNumberObservable.onNext(this.interfaceNumber += variation);
    }

    public Observable<Integer> getPackageNumberObservable() {
        return packageNumberObservable;
    }

    public Observable<Integer> getClassNumberObservable() {
        return classNumberObservable;
    }

    public Observable<Integer> getInterfaceNumberObservable() {
        return interfaceNumberObservable;
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

    public void resetCounters() {
        this.packageNumber = 0;
        this.classNumber = 0;
        this.interfaceNumber = 0;
        this.packageNumberObservable.onNext(this.packageNumber);
        this.classNumberObservable.onNext(this.classNumber);
        this.interfaceNumberObservable.onNext(this.interfaceNumber);
    }
}
