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

    public ProjectAnalyzerImpl() {
        this.viewController = new ViewController(this);
        this.alreadyAnalyzed = new ArrayList<>();
        Vertx.vertx().deployVerticle(this);
    }

    @Override
    public Future<InterfaceReport> getInterfaceReport(String srcInterfacePath) {
        return this.getVertx().executeBlocking(promise -> {
            if (!this.controlFileAnalyzed(srcInterfacePath)) {
                this.addFileAnalyzed(srcInterfacePath);
                CompilationUnit compilationUnit;
                try {
                    compilationUnit = StaticJavaParser.parse(new File(srcInterfacePath));
                } catch (FileNotFoundException e) {
                    throw new RuntimeException(e);
                }
                InterfaceReportImpl interfaceReport = new InterfaceReportImpl();
                InterfaceCollector interfaceCollector = new InterfaceCollector();
                interfaceCollector.visit(compilationUnit, interfaceReport);
                this.viewController.increaseInterfaceNumber();
                promise.complete(interfaceReport);
            } else {
                promise.complete();
            }
        });
    }

    @Override
    public Future<ClassReport> getClassReport(String srcClassPath, SimpleTreeNode fatherTreeNode) {
        return this.getVertx().executeBlocking(promise -> {
            if (!this.controlFileAnalyzed(srcClassPath)) {
                this.addFileAnalyzed(srcClassPath);
                CompilationUnit compilationUnit;
                try {
                    compilationUnit = StaticJavaParser.parse(new File(srcClassPath));
                } catch (FileNotFoundException e) {
                    throw new RuntimeException(e);
                }
                ClassReportImpl classReport = new ClassReportImpl();
                ClassCollector classCollector = new ClassCollector();
                classCollector.visit(compilationUnit, classReport);
                this.viewController.increaseClassNumber();
                if (classReport.getInnerClass() != null) {
                    this.addInnerChildClassNodeToFather(classReport, fatherTreeNode);
                }
                promise.complete(classReport);
            } else {
                promise.complete();
            }
        });
    }

    @Override
    public Future<PackageReport> getPackageReport(String srcPackagePath, Consumer<ProjectElem> callback, SimpleTreeNode fatherTreeNode) {
        return this.getVertx().executeBlocking(promise -> {
            PackageDeclaration packageDeclaration;
            packageDeclaration = StaticJavaParser.parsePackageDeclaration("package " + srcPackagePath + ";");
            PackageReportImpl packageReport = new PackageReportImpl();
            packageReport.setFullPackageName(packageDeclaration.getNameAsString());

            // ci salviamo le unita di memoria che contengono i dati rilevanti sulle classi e interfacce
            List<CompilationUnit> classesOrInterfacesUnit = this.createParsedFileList(packageDeclaration).stream()
                    .filter(r -> r.isSuccessful() && r.getResult().isPresent())
                    .map(r -> r.getResult().get())
                    .collect(Collectors.toList());

            // ci salviamo le strutture dati con cui riempiremo il nostro packageReport
            List<ClassReport> classReports = new ArrayList<>();
            List<InterfaceReport> interfaceReports = new ArrayList<>();

            List<Future> futureListClass = new ArrayList<>();
            List<Future> futureListInterface = new ArrayList<>();


            // analizziamo le classi o le interfacce in maniera asincrona
            for (CompilationUnit cu : classesOrInterfacesUnit) {
                // prendiamo tutte le dichiarazione delle classi/interface
                List<ClassOrInterfaceDeclaration> declarationList = cu.getTypes().stream()
                        .map(TypeDeclaration::asTypeDeclaration)
                        .filter(BodyDeclaration::isClassOrInterfaceDeclaration)
                        .map(ClassOrInterfaceDeclaration.class::cast)
                        .collect(Collectors.toList());

                // riempiamo le future in modo tale da sapere in futuro quando saranno pronte le variabili
                for (ClassOrInterfaceDeclaration declaration : declarationList) {
                    if (declaration.isInterface()) {
                        SimpleTreeNode interfaceNodeChild = new SimpleTreeNode("Interface child: " +
                                declaration.getNameAsString());
                        fatherTreeNode.addChild(interfaceNodeChild);
                        Future<InterfaceReport> interfaceReportFuture = this.getInterfaceReport(ProjectAnalyzerImpl.PATH +
                                "/" + declaration.getFullyQualifiedName().get()
                                .replace(".", "/") + ".java").onComplete(res ->{
                                    if(res.result() != null){
                                        callback.accept(res.result());
                                    }
                        });
                        futureListInterface.add(interfaceReportFuture);
                    } else {
                        SimpleTreeNode classNodeChild = new SimpleTreeNode("Class child: " +
                                declaration.getNameAsString());
                        fatherTreeNode.addChild(classNodeChild);
                        Future<ClassReport> classReportFuture = this.getClassReport(ProjectAnalyzerImpl.PATH +
                                "/" + declaration.getFullyQualifiedName().get()
                                .replace(".", "/") + ".java", classNodeChild).onComplete(res ->{
                            if(res.result() != null){
                                callback.accept(res.result());
                            }
                        });
                        futureListClass.add(classReportFuture);
                    }
                }
            }

            // aspettiamo che sia le classi sia le interfacce siano pronte e poi riempiamo la package report con le info
            CompositeFuture.all(futureListClass).onComplete(res -> {
                this.log("onComplete - Class Report");
                futureListClass.forEach(c -> classReports.add((ClassReport) c.result()));
                packageReport.setClassReports(classReports);
            });

            CompositeFuture.all(futureListInterface).onComplete(res -> {
                this.log("onComplete - Interface Report");
                futureListInterface.forEach(c -> interfaceReports.add((InterfaceReport) c.result()));
                packageReport.setInterfaceReports(interfaceReports);
            });

            List<Future> futureListClassInterface = Stream.concat(futureListClass.stream(), futureListInterface.stream()).collect(Collectors.toList());

            CompositeFuture.all(futureListClassInterface).onComplete(res -> {
                this.log("onComplete - Class/Interface Report");
                callback.accept(packageReport);
                this.viewController.increasePackageNumber();
                promise.complete(packageReport);
            });
        });
    }

    @Override
    public void analyzeProject(String srcProjectFolderName, Consumer<ProjectElem> callback) {
        // ci creiamo la struttura dati del project report
        ProjectReportImpl projectReport = new ProjectReportImpl();
        List<Future> futureListPackage = new ArrayList<>();
        List<PackageReport> packageReports = new ArrayList<>();

        // mi preparo il path da cui deve partire l'analisi
        SourceRoot sourceRoot = new SourceRoot(Paths.get(srcProjectFolderName)).setParserConfiguration(new ParserConfiguration());
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

        SimpleTreeNode rootProject = new SimpleTreeNode("Root Project: " + srcProjectFolderName);

        // mi preparo una lista di future per i futuri package
        for (PackageDeclaration packageDeclaration : allCus) {
            SimpleTreeNode packageNodeChild = new SimpleTreeNode("Package child: " + packageDeclaration.getNameAsString());
            rootProject.addChild(packageNodeChild);
            Future<PackageReport> packageReportFuture = this.getPackageReport(packageDeclaration.getNameAsString(),
                    callback, packageNodeChild).onComplete(res ->{
                if(res.result() != null){
                    callback.accept(res.result());
                }
            });
            futureListPackage.add(packageReportFuture);
        }

        // aspetto che tutte le future siano pronte e restituisco il project report
        CompositeFuture.all(futureListPackage).onComplete(res -> {
            this.log("onComplete - Package");
            futureListPackage.forEach(c -> packageReports.add((PackageReport) c.result()));
            projectReport.setPackageReports(packageReports);
            this.viewController.log(ListingTreePrinter.builder().ascii().build().stringify(rootProject));
        });
    }

    private List<ParseResult<CompilationUnit>> createParsedFileList(PackageDeclaration dec) {
        SourceRoot sourceRoot = new SourceRoot(Paths.get(ProjectAnalyzerImpl.PATH)).setParserConfiguration(new ParserConfiguration());
        List<ParseResult<CompilationUnit>> parseResultList;
        parseResultList = sourceRoot.tryToParseParallelized(dec.getNameAsString());
        return parseResultList;
    }

    private synchronized boolean controlFileAnalyzed(String srcFilePath) {
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
        SimpleTreeNode innerClassChildNode = new SimpleTreeNode("Inner Class child: " + innerClass.getFullClassName());
        fatherTreeNode.addChild(innerClassChildNode);
        if (innerClass.getInnerClass() != null) {
            this.addInnerChildClassNodeToFather(innerClass, fatherTreeNode);
        }
    }

    private void log(String msg) {
        System.out.println("[THREAD] " + Thread.currentThread() + msg);
    }
}
