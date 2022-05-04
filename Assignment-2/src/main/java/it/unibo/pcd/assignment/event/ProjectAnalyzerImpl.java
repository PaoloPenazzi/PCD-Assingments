package it.unibo.pcd.assignment.event;

import com.github.javaparser.ParseResult;
import com.github.javaparser.ParserConfiguration;
import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.PackageDeclaration;
import com.github.javaparser.ast.body.BodyDeclaration;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.TypeDeclaration;
import com.github.javaparser.utils.SourceRoot;
import hu.webarticum.treeprinter.SimpleTreeNode;
import hu.webarticum.treeprinter.printer.listing.ListingTreePrinter;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.CompositeFuture;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import it.unibo.pcd.assignment.event.collector.ClassCollector;
import it.unibo.pcd.assignment.event.collector.InterfaceCollector;
import it.unibo.pcd.assignment.event.report.*;
import it.unibo.pcd.assignment.event.view.ViewController;

import java.io.File;
import java.io.FileNotFoundException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ProjectAnalyzerImpl extends AbstractVerticle implements ProjectAnalyzer {
    public static String PATH = "";
    private final ViewController viewController;
    private final List<String> alreadyAnalyzed;

    //TODO SISTEMARE LA LISTA DEI FILE GIÀ ANALIZZATI MERDAAA

    public ProjectAnalyzerImpl() {
        this.viewController = new ViewController(this);
        this.alreadyAnalyzed = new ArrayList<>();
        Vertx.vertx().deployVerticle(this);
    }

    @Override
    public Future<InterfaceReport> getInterfaceReport(String srcInterfacePath, SimpleTreeNode fatherTreeNode) {
        return this.getVertx().executeBlocking(promise -> {
            CompilationUnit compilationUnit = this.parseSingleFile(srcInterfacePath);
            InterfaceReportImpl interfaceReport = new InterfaceReportImpl();
            InterfaceCollector interfaceCollector = new InterfaceCollector();
            interfaceCollector.visit(compilationUnit, interfaceReport);
            this.viewController.increaseInterfaceNumber();
            promise.complete(interfaceReport);
        });
    }

    @Override
    public Future<ClassReport> getClassReport(String srcClassPath, SimpleTreeNode fatherTreeNode) {
        return this.getVertx().executeBlocking(promise -> {
            CompilationUnit compilationUnit = this.parseSingleFile(srcClassPath);
            ClassReportImpl classReport = new ClassReportImpl();
            ClassCollector classCollector = new ClassCollector();
            classCollector.visit(compilationUnit, classReport);
            this.viewController.increaseClassNumber();
            if (classReport.getInnerClass() != null) {
                this.addInnerChildClassNodeToFather(classReport, fatherTreeNode);
            }
            promise.complete(classReport);
        });
    }

    @Override
    public Future<PackageReport> getPackageReport(String srcPackagePath, SimpleTreeNode fatherTreeNode) {
        return this.getVertx().executeBlocking(promise -> {
            List<ClassReport> classReports = new ArrayList<>();
            List<InterfaceReport> interfaceReports = new ArrayList<>();
            List<Future> futureListClass = new ArrayList<>();
            List<Future> futureListInterface = new ArrayList<>();
            PackageReportImpl packageReport = new PackageReportImpl();

            PackageDeclaration packageDeclaration = StaticJavaParser.parsePackageDeclaration("package " + srcPackagePath + ";");
            packageReport.setFullPackageName(srcPackagePath);

            List<CompilationUnit> classesOrInterfacesUnit = this.createParsedFileList(packageDeclaration).stream()
                    .filter(r -> r.isSuccessful() && r.getResult().isPresent())
                    .map(r -> r.getResult().get())
                    .collect(Collectors.toList());

            for (CompilationUnit cu : classesOrInterfacesUnit) {
                List<ClassOrInterfaceDeclaration> declarationList = cu.getTypes().stream()
                        .map(TypeDeclaration::asTypeDeclaration)
                        .filter(BodyDeclaration::isClassOrInterfaceDeclaration)
                        .map(ClassOrInterfaceDeclaration.class::cast)
                        .collect(Collectors.toList());

                for (ClassOrInterfaceDeclaration declaration : declarationList) {
                    String srcFilePath = ProjectAnalyzerImpl.PATH + "/" + declaration.getFullyQualifiedName().get()
                            .replace(".", "/") + ".java";
                    if (declaration.getFullyQualifiedName().isPresent() && !this.isFileAlreadyAnalyzed(srcFilePath)) {
                        this.addFileAnalyzed(srcFilePath);
                        if (declaration.isInterface()) {
                            SimpleTreeNode interfaceNodeChild = new SimpleTreeNode("Interface child: " + srcFilePath);
                            fatherTreeNode.addChild(interfaceNodeChild);
                            futureListInterface.add(this.getInterfaceReport(srcFilePath,interfaceNodeChild));
                        } else {
                            SimpleTreeNode classNodeChild = new SimpleTreeNode("Class child: " + srcFilePath);
                            fatherTreeNode.addChild(classNodeChild);
                            futureListClass.add(this.getClassReport(srcFilePath, classNodeChild));
                        }
                    }
                }
            }

            // aspetto che tutte le classi siano pronte
            CompositeFuture.all(futureListClass).onComplete(res -> {
                futureListClass.forEach(c -> classReports.add((ClassReport) c.result()));
                packageReport.setClassReports(classReports);
            });

            // aspetto che tutte le interfacce siano pronte
            CompositeFuture.all(futureListInterface).onComplete(res -> {
                futureListInterface.forEach(c -> interfaceReports.add((InterfaceReport) c.result()));
                packageReport.setInterfaceReports(interfaceReports);
            });

            // aspetto che tutte le classi e le interfacce siano pronte per caricare il report
            CompositeFuture.all(Stream.concat(futureListClass.stream(),
                    futureListInterface.stream()).collect(Collectors.toList())).onComplete(res -> {
                        this.viewController.increasePackageNumber();
                        promise.complete(packageReport);
            });
        });
    }

    @Override
    public Future<ProjectReport> getProjectReport(String srcProjectPath) {
        return this.getVertx().executeBlocking(promise -> {
            // ci creiamo la struttura dati del project report
            ProjectReportImpl projectReport = new ProjectReportImpl();
            List<Future> futureListPackage = new ArrayList<>();
            List<PackageReport> packageReports = new ArrayList<>();

            // mi preparo il path da cui deve partire l'analisi
            SourceRoot sourceRoot = new SourceRoot(Paths.get(srcProjectPath)).setParserConfiguration(new ParserConfiguration());
            List<ParseResult<CompilationUnit>> parseResultList;
            parseResultList = sourceRoot.tryToParseParallelized();

            // mi prendo i vari package che compongono il progetto
            List<PackageDeclaration> allCus = parseResultList.stream()
                    .filter(r -> r.getResult().isPresent() && r.isSuccessful())
                    .map(r -> r.getResult().get())
                    .filter(c -> c.getPackageDeclaration().isPresent())
                    .map(c -> c.getPackageDeclaration().get())
                    .distinct()
                    .collect(Collectors.toList());

            SimpleTreeNode rootProject = new SimpleTreeNode("Root Project: " + srcProjectPath);

            // mi preparo una lista di future per i futuri package
            for (PackageDeclaration packageDeclaration : allCus) {
                String packageName = packageDeclaration.getNameAsString();
                SimpleTreeNode packageNodeChild = new SimpleTreeNode("Package child: " + packageName);
                rootProject.addChild(packageNodeChild);
                futureListPackage.add(this.getPackageReport(packageName, packageNodeChild));
            }

            // aspetto che tutte le future siano pronte e restituisco il project report
            CompositeFuture.all(futureListPackage).onComplete(res -> {
                futureListPackage.forEach(c -> packageReports.add((PackageReport) c.result()));
                projectReport.setPackageReports(packageReports);
                this.viewController.log(projectReport.toString());
                this.viewController.log(ListingTreePrinter.builder().ascii().build().stringify(rootProject));
                promise.complete(projectReport);
            });
        });
    }

    @Override
    public void analyzeProject(String srcProjectFolderName, Consumer<ProjectElem> callback) {
        this.getVertx().executeBlocking(promise -> {
            // mi preparo il path da cui deve partire l'analisi
            SourceRoot sourceRoot = new SourceRoot(Paths.get(srcProjectFolderName)).setParserConfiguration(new ParserConfiguration());
            List<ParseResult<CompilationUnit>> parseResultList = sourceRoot.tryToParseParallelized();

            // mi prendo i vari package che compongono il progetto
            List<PackageDeclaration> allCus = parseResultList.stream()
                    .filter(r -> r.getResult().isPresent() && r.isSuccessful())
                    .map(r -> r.getResult().get())
                    .filter(c -> c.getPackageDeclaration().isPresent())
                    .map(c -> c.getPackageDeclaration().get())
                    .distinct()
                    .collect(Collectors.toList());

            SimpleTreeNode rootProject = new SimpleTreeNode("Root Project: " + srcProjectFolderName);
            this.viewController.log("Root Project: " + srcProjectFolderName);
            this.viewController.log(ListingTreePrinter.builder().ascii().build().stringify(rootProject));

            // mi preparo una lista di future per i futuri package
            for (PackageDeclaration packageDeclaration : allCus) {
                SimpleTreeNode packageNodeChild = new SimpleTreeNode("Package child: " + packageDeclaration.getNameAsString());
                rootProject.addChild(packageNodeChild);
                this.viewController.log("\t Package Name: " + packageDeclaration.getNameAsString());
                this.viewController.log(ListingTreePrinter.builder().ascii().build().stringify(rootProject));

                // ci salviamo le unita di memoria che contengono i dati rilevanti sulle classi e interfacce
                List<CompilationUnit> classesOrInterfacesUnit = this.createParsedFileList(packageDeclaration).stream()
                        .filter(r -> r.isSuccessful() && r.getResult().isPresent())
                        .map(r -> r.getResult().get())
                        .collect(Collectors.toList());

                // analizziamo le classi o le interfacce in maniera asincrona
                for (CompilationUnit cu : classesOrInterfacesUnit) {
                    // prendiamo tutte le dichiarazione delle classi/interface
                    List<ClassOrInterfaceDeclaration> declarationList = cu.getTypes().stream()
                            .map(TypeDeclaration::asTypeDeclaration)
                            .filter(BodyDeclaration::isClassOrInterfaceDeclaration)
                            .map(ClassOrInterfaceDeclaration.class::cast)
                            .collect(Collectors.toList());

                    for (ClassOrInterfaceDeclaration declaration : declarationList) {
                        if (declaration.getFullyQualifiedName().isPresent()) {
                            if (declaration.isInterface()) {
                                SimpleTreeNode interfaceNodeChild = new SimpleTreeNode("Interface child: " +
                                        declaration.getNameAsString());
                                packageNodeChild.addChild(interfaceNodeChild);
                                this.viewController.log(ListingTreePrinter.builder().ascii().build().stringify(rootProject));
                                this.getInterfaceReport(ProjectAnalyzerImpl.PATH +
                                        "/" + declaration.getFullyQualifiedName().get().
                                        replace(".", "/") + ".java", interfaceNodeChild).onComplete(res -> {
                                    if (res.result() != null) {
                                        callback.accept(res.result());
                                    }
                                });
                            } else {
                                SimpleTreeNode classNodeChild = new SimpleTreeNode("Class child: " +
                                        declaration.getNameAsString());
                                packageNodeChild.addChild(classNodeChild);
                                this.viewController.log(ListingTreePrinter.builder().ascii().build().stringify(rootProject));
                                this.getClassReport(ProjectAnalyzerImpl.PATH +
                                        "/" + declaration.getFullyQualifiedName().get()
                                        .replace(".", "/") + ".java", classNodeChild).onComplete(res -> {
                                    if (res.result() != null) {
                                        callback.accept(res.result());
                                    }
                                });
                            }
                        }
                    }
                }
            }
        });
    }

    private List<ParseResult<CompilationUnit>> createParsedFileList(PackageDeclaration dec) {
        SourceRoot sourceRoot = new SourceRoot(Paths.get(ProjectAnalyzerImpl.PATH)).setParserConfiguration(new ParserConfiguration());
        List<ParseResult<CompilationUnit>> parseResultList;
        parseResultList = sourceRoot.tryToParseParallelized(dec.getNameAsString());
        return parseResultList;
    }

    private synchronized boolean isFileAlreadyAnalyzed(String srcFilePath) {
        return this.alreadyAnalyzed.contains(srcFilePath);
    }

    private synchronized void addFileAnalyzed(String srcFilePath) {
        this.alreadyAnalyzed.add(srcFilePath);
    }

    public List<String> getAlreadyAnalyzed() {
        return alreadyAnalyzed;
    }

    private void addInnerChildClassNodeToFather(ClassReport classReport, SimpleTreeNode fatherTreeNode) {
        ClassReport innerClass = classReport.getInnerClass();
        this.viewController.increaseClassNumber();
        SimpleTreeNode innerClassChildNode = new SimpleTreeNode("Inner Class child: " + innerClass.getFullClassName());
        fatherTreeNode.addChild(innerClassChildNode);
        if (innerClass.getInnerClass() != null) {
            this.addInnerChildClassNodeToFather(innerClass, fatherTreeNode);
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

    private void log(String msg) {
        System.out.println("[THREAD] " + Thread.currentThread() + msg);
    }
}
