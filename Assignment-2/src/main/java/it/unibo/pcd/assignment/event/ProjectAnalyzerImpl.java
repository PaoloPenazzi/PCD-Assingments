package it.unibo.pcd.assignment.event;

import com.github.javaparser.ParseResult;
import com.github.javaparser.ParserConfiguration;
import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.PackageDeclaration;
import com.github.javaparser.ast.body.BodyDeclaration;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.TypeDeclaration;
import com.github.javaparser.utils.Pair;
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
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ProjectAnalyzerImpl extends AbstractVerticle implements ProjectAnalyzer {
    private String PATH;
    private final ViewController viewController;
    private final String separator = System.getProperty("file.separator");
    private final int DELAY_MILLIS = 50;

    public ProjectAnalyzerImpl() {
        this.PATH = "";
        this.viewController = new ViewController(this);
        Vertx.vertx().deployVerticle(this);
    }

    @Override
    public Future<InterfaceReport> getInterfaceReport(String interfacePath, SimpleTreeNode fatherTreeNode) {
        return this.getVertx().executeBlocking(promise -> {
            this.delay(this.DELAY_MILLIS);
            CompilationUnit compilationUnit = this.parseSingleFile(interfacePath);
            InterfaceReportImpl interfaceReport = new InterfaceReportImpl();
            InterfaceCollector interfaceCollector = new InterfaceCollector();
            interfaceCollector.visit(compilationUnit, interfaceReport);
            promise.complete(interfaceReport);
        });
    }

    @Override
    public Future<ClassReport> getClassReport(String classPath, SimpleTreeNode fatherTreeNode) {
        return this.getVertx().executeBlocking(promise -> {
            this.delay(this.DELAY_MILLIS);
            CompilationUnit compilationUnit = this.parseSingleFile(classPath);
            ClassReportImpl classReport = new ClassReportImpl();
            ClassCollector classCollector = new ClassCollector();
            classCollector.visit(compilationUnit, classReport);
            if (classReport.getInnerClass() != null) {
                this.addInnerChildClassNodeToFather(classReport, fatherTreeNode);
            }
            promise.complete(classReport);
        });
    }

    @Override
    public Future<PackageReport> getPackageReport(String packagePath, SimpleTreeNode fatherTreeNode) {
        return this.getVertx().executeBlocking(promise -> {
            List<ClassReport> classReports = new ArrayList<>();
            List<InterfaceReport> interfaceReports = new ArrayList<>();
            List<Future> futureListClass = new ArrayList<>();
            List<Future> futureListInterface = new ArrayList<>();
            PackageReportImpl packageReport = new PackageReportImpl();

            this.printTree(fatherTreeNode);
            packageReport.setFullPackageName(packagePath);

            for (CompilationUnit cu : this.getClassesOrInterfacesUnit(this.PATH)) {
                for (ClassOrInterfaceDeclaration declaration : this.getClassOrInterfaceDeclarationList(cu)) {
                    String srcFilePath = this.PATH + "\\" + declaration.getName().toString() + ".java";
                    if (declaration.getFullyQualifiedName().isPresent()) {
                        if (declaration.isInterface()) {
                            this.viewController.increaseInterfaceNumber();
                            futureListInterface.add(this.launchInterfaceReport(srcFilePath, fatherTreeNode, fatherTreeNode));
                        } else {
                            this.viewController.increaseClassNumber();
                            futureListClass.add(this.launchClassReport(srcFilePath, fatherTreeNode, fatherTreeNode));
                        }
                    }
                }
            }

            CompositeFuture.all(Stream.concat(futureListClass.stream(),
                    futureListInterface.stream()).collect(Collectors.toList())).onComplete(res -> {
                        this.viewController.increasePackageNumber();
                        futureListClass.forEach(c -> classReports.add((ClassReport) c.result()));
                        packageReport.setClassReports(classReports);
                        futureListInterface.forEach(c -> interfaceReports.add((InterfaceReport) c.result()));
                        packageReport.setInterfaceReports(interfaceReports);
                        promise.complete(packageReport);
            });
        });
    }

    @Override
    public Future<ProjectReport> getProjectReport(String srcProjectPath) {
        return this.getVertx().executeBlocking(promise -> {
            ProjectReportImpl projectReport = new ProjectReportImpl();
            List<Future> futureList = new ArrayList<>();
            List<PackageReport> packageReports = new ArrayList<>();
            List<Pair<String, Boolean>> pairList = new ArrayList<>();
            List<Pair<String, String>> pairs = new ArrayList<>();

            projectReport.setProjectName(srcProjectPath);

            SourceRoot sourceRoot = new SourceRoot(Paths.get(srcProjectPath)).setParserConfiguration(new ParserConfiguration());
            List<ParseResult<CompilationUnit>> parseResultList;
            parseResultList = sourceRoot.tryToParseParallelized();

            List<PackageDeclaration> allCus = parseResultList.stream()
                    .filter(r -> r.getResult().isPresent() && r.isSuccessful())
                    .map(r -> r.getResult().get())
                    .filter(c -> c.getPackageDeclaration().isPresent())
                    .map(c -> c.getPackageDeclaration().get())
                    .distinct()
                    .collect(Collectors.toList());

            SimpleTreeNode rootProject = new SimpleTreeNode("Root Project: " + srcProjectPath);

            for (PackageDeclaration packageDeclaration : allCus) {
                List<Future> futureListClass = new ArrayList<>();
                List<Future> futureListInterface = new ArrayList<>();
                List<ClassReport> classReports = new ArrayList<>();
                List<InterfaceReport> interfaceReports = new ArrayList<>();
                PackageReportImpl packageReport = new PackageReportImpl();

                this.viewController.increasePackageNumber();
                SimpleTreeNode packageNodeChild = new SimpleTreeNode("Package child: " + packageDeclaration.getNameAsString());
                rootProject.addChild(packageNodeChild);
                printTree(rootProject);

                List<CompilationUnit> classesOrInterfacesUnit = getClassesOrInterfacesUnit(this.PATH);

                for (CompilationUnit cu : classesOrInterfacesUnit) {
                    List<ClassOrInterfaceDeclaration> declarationList = getClassOrInterfaceDeclarationList(cu);

                    for (ClassOrInterfaceDeclaration declaration : declarationList) {
                        String srcFilePath = this.PATH + this.separator + declaration.getFullyQualifiedName().get()
                                .replace(".", this.separator) + ".java";
                        if (declaration.getFullyQualifiedName().isPresent() && this.isRightPackage(packageDeclaration.getNameAsString(), declaration)) {
                            if (declaration.isInterface()) {
                                SimpleTreeNode interfaceNodeChild = new SimpleTreeNode("Interface child: " + srcFilePath);
                                packageNodeChild.addChild(interfaceNodeChild);
                                this.viewController.increaseInterfaceNumber();
                                printTree(rootProject);
                                this.delay(this.DELAY_MILLIS);
                                futureListInterface.add(this.getInterfaceReport(srcFilePath,interfaceNodeChild));
                            } else {
                                SimpleTreeNode classNodeChild = new SimpleTreeNode("Class child: " + srcFilePath);
                                packageNodeChild.addChild(classNodeChild);
                                this.viewController.increaseClassNumber();
                                printTree(rootProject);
                                this.delay(this.DELAY_MILLIS);
                                futureListClass.add(this.getClassReport(srcFilePath, classNodeChild).onComplete(res -> {
                                    pairList.add(new Pair<>(res.result().getSrcFullFileName(), res.result().getMethodsInfo().stream().anyMatch(MethodInfo::isMain)));
                                }));
                            }
                        }
                    }
                }

                CompositeFuture.all(futureListClass).onComplete(res -> {
                    futureListClass.forEach(c -> classReports.add((ClassReport) c.result()));
                    packageReport.setClassReports(classReports);
                });

                // aspetto che tutte le interfacce siano pronte
                CompositeFuture.all(futureListInterface).onComplete(res -> {
                    futureListInterface.forEach(c -> interfaceReports.add((InterfaceReport) c.result()));
                    packageReport.setInterfaceReports(interfaceReports);
                });

                List<Future> futureListTemp = Stream.concat(futureListInterface.stream(), futureListClass.stream()).collect(Collectors.toList());
                futureList = Stream.concat(futureListTemp.stream(), futureList.stream()).collect(Collectors.toList());

                CompositeFuture.all(Stream.concat(futureListClass.stream(),
                        futureListInterface.stream()).collect(Collectors.toList())).onComplete(res -> {
                    packageReports.add(packageReport);
                });

            }

            CompositeFuture.all(futureList).onComplete(res -> {
                for (Pair<String, Boolean> booleanPair : pairList) {
                    for (PackageDeclaration packageDeclaration : allCus) {
                        if (booleanPair.a.contains(packageDeclaration.getNameAsString())) {
                            if (booleanPair.b) {
                                Pair<String, String> stringStringPair = new Pair<>(packageDeclaration.getNameAsString(), booleanPair.a);
                                pairs.add(stringStringPair);
                            }
                        }
                    }
                }
                projectReport.setPairList(pairs);
                projectReport.setPackageReports(packageReports);
                this.viewController.log(ListingTreePrinter.builder().ascii().build().stringify(rootProject));
                promise.complete(projectReport);
            });
        });
    }

    @Override
    public void analyzeProject(String srcProjectFolderName, Consumer<ProjectElem> callback) {
        this.getVertx().executeBlocking(promise -> {
            List<PackageDeclaration> allCus = getPackageDeclarationList(srcProjectFolderName);

            SimpleTreeNode rootProject = new SimpleTreeNode("Root Project: " + srcProjectFolderName);
            this.viewController.log(ListingTreePrinter.builder().ascii().build().stringify(rootProject));

            for (PackageDeclaration packageDeclaration : allCus) {
                this.viewController.increasePackageNumber();
                SimpleTreeNode packageNodeChild = new SimpleTreeNode("Package child: " + packageDeclaration.getNameAsString());
                rootProject.addChild(packageNodeChild);
                this.printTree(rootProject);

                for (CompilationUnit cu : this.getClassesOrInterfacesUnit(this.PATH)) {
                    for (ClassOrInterfaceDeclaration declaration : this.getClassOrInterfaceDeclarationList(cu)) {
                        String srcFilePath = this.PATH + this.separator + declaration.getFullyQualifiedName().get()
                                .replace(".", this.separator) + ".java";
                        if (declaration.getFullyQualifiedName().isPresent() && this.isRightPackage(packageDeclaration.getNameAsString(), declaration)) {
                            if (declaration.isInterface()) {
                                this.viewController.increaseInterfaceNumber();
                                this.launchInterfaceReport(srcFilePath, packageNodeChild, rootProject).onComplete(res -> {
                                    if (res.result() != null) {
                                        callback.accept(res.result());
                                    }
                                });
                            } else {
                                this.viewController.increaseClassNumber();
                                this.launchClassReport(srcFilePath, packageNodeChild, rootProject).onComplete(res -> {
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

    private List<ClassOrInterfaceDeclaration> getClassOrInterfaceDeclarationList(CompilationUnit cu) {
        return cu.getTypes().stream()
                .map(TypeDeclaration::asTypeDeclaration)
                .filter(BodyDeclaration::isClassOrInterfaceDeclaration)
                .map(ClassOrInterfaceDeclaration.class::cast)
                .collect(Collectors.toList());
    }

    private List<CompilationUnit> getClassesOrInterfacesUnit(String pathToParse) {
        return this.createParsedFileList(pathToParse).stream()
                .filter(r -> r.isSuccessful() && r.getResult().isPresent())
                .map(r -> r.getResult().get())
                .collect(Collectors.toList());
    }

    private Future<InterfaceReport> launchInterfaceReport(String srcFilePath, SimpleTreeNode fatherNodeToAttach, SimpleTreeNode rootNodeToPrint){
        SimpleTreeNode interfaceNodeChild = new SimpleTreeNode("Interface child: " + srcFilePath);
        fatherNodeToAttach.addChild(interfaceNodeChild);
        printTree(rootNodeToPrint);
        this.delay(this.DELAY_MILLIS);
        return this.getInterfaceReport(srcFilePath,interfaceNodeChild);
    }

    private Future<ClassReport> launchClassReport(String srcFilePath, SimpleTreeNode fatherNodeToAttach, SimpleTreeNode rootNodeToPrint){
        SimpleTreeNode classNodeChild = new SimpleTreeNode("Class child: " + srcFilePath);
        fatherNodeToAttach.addChild(classNodeChild);
        printTree(rootNodeToPrint);
        this.delay(this.DELAY_MILLIS);
        return this.getClassReport(srcFilePath,classNodeChild);
    }

    private void printTree(SimpleTreeNode rootProject) {
        this.viewController.clearScreen();
        this.viewController.log(ListingTreePrinter.builder().ascii().build().stringify(rootProject));
    }

    public static List<PackageDeclaration> getPackageDeclarationList(String srcProjectFolderName) {
        SourceRoot sourceRoot = new SourceRoot(Paths.get(srcProjectFolderName)).setParserConfiguration(new ParserConfiguration());
        List<ParseResult<CompilationUnit>> parseResultList = sourceRoot.tryToParseParallelized();

        return parseResultList.stream()
                .filter(r -> r.getResult().isPresent() && r.isSuccessful())
                .map(r -> r.getResult().get())
                .filter(c -> c.getPackageDeclaration().isPresent())
                .map(c -> c.getPackageDeclaration().get())
                .distinct()
                .collect(Collectors.toList());
    }

    public String getPATH() {
        return this.PATH;
    }

    public void setPATH(String PATH) {
        this.PATH = PATH;
    }

    public ViewController getViewController() {
        return this.viewController;
    }

    private void delay(int millis){
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private boolean isRightPackage(String packageName, ClassOrInterfaceDeclaration declaration) {
        String classFullName = declaration.getFullyQualifiedName().isPresent() ? declaration.getFullyQualifiedName().get() : "ERROR!";
        String className = declaration.getNameAsString();
        classFullName = classFullName.replace("/" + className, "");
        classFullName = classFullName.replace("." + className, "");
        return classFullName.equals(packageName);
    }

    private List<ParseResult<CompilationUnit>> createParsedFileList(String sourceRootPath) {
        SourceRoot sourceRoot = new SourceRoot(Paths.get(sourceRootPath)).setParserConfiguration(new ParserConfiguration());
        return sourceRoot.tryToParseParallelized();
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

    private List<ParseResult<CompilationUnit>> createParsedFileList(PackageDeclaration dec, String sourceRootPath) {
        SourceRoot sourceRoot = new SourceRoot(Paths.get(sourceRootPath)).setParserConfiguration(new ParserConfiguration());
        return sourceRoot.tryToParseParallelized(dec.getNameAsString());
    }
}
