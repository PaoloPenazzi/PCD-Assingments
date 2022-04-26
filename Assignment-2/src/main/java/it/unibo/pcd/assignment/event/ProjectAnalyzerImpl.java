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
    private final ViewController viewController;
    public static String PATH = "";

    public ProjectAnalyzerImpl() {
        this.viewController = new ViewController();
        Vertx.vertx().deployVerticle(this);
    }

    @Override
    public Future<InterfaceReport> getInterfaceReport(String srcInterfacePath, Consumer<ProjectElem> callback) {
        return this.getVertx().executeBlocking(promise -> {
            CompilationUnit compilationUnit;
            try {
                compilationUnit = StaticJavaParser.parse(new File(srcInterfacePath));
            } catch (FileNotFoundException e) {
                throw new RuntimeException(e);
            }
            InterfaceReportImpl interfaceReport = new InterfaceReportImpl();
            InterfaceCollector interfaceCollector = new InterfaceCollector();
            interfaceCollector.visit(compilationUnit, interfaceReport);
            callback.accept(interfaceReport);
            promise.complete(interfaceReport);
        });
    }

    @Override
    public Future<ClassReport> getClassReport(String srcClassPath, Consumer<ProjectElem> callback) {
        return this.getVertx().executeBlocking(promise -> {
            CompilationUnit compilationUnit;
            try {
                compilationUnit = StaticJavaParser.parse(new File(srcClassPath));
            } catch (FileNotFoundException e) {
                throw new RuntimeException(e);
            }
            ClassReportImpl classReport = new ClassReportImpl();
            ClassCollector classCollector = new ClassCollector();
            classCollector.visit(compilationUnit, classReport);
            callback.accept(classReport);
            promise.complete(classReport);
        });
    }

    @Override
    public Future<PackageReport> getPackageReport(String srcPackagePath, Consumer<ProjectElem> callback) {
        return this.getVertx().executeBlocking(promise -> {
            PackageDeclaration packageDeclaration;
            packageDeclaration = StaticJavaParser.parsePackageDeclaration("package " + srcPackagePath + ";");
            PackageReportImpl packageReport = new PackageReportImpl();

            // salviamo il  nome del package
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
                        .map(x -> (ClassOrInterfaceDeclaration) x)
                        .collect(Collectors.toList());
                // riempiamo le future in modo tale da sapere in futuro quando saranno pronte le variabili
                for (ClassOrInterfaceDeclaration declaration : declarationList) {
                    if (declaration.isInterface()) {
                        futureListInterface.add(this.getInterfaceReport("src/main/java/" + declaration.getFullyQualifiedName().get()
                                .replace(".", "/") + ".java", callback));
                    } else {
                        futureListClass.add(this.getClassReport("src/main/java/" + declaration.getFullyQualifiedName().get()
                                .replace(".", "/") + ".java", callback));
                    }
                }
            }

            // aspettiamo che sia le classi sia le interfacce siano pronte e poi riempiamo la package report con le info
            CompositeFuture.all(futureListClass).onComplete(res -> {
                futureListClass.forEach(c -> classReports.add((ClassReport) c.result()));
                packageReport.setClassReports(classReports);
            });

            CompositeFuture.all(futureListInterface).onComplete(res -> {
                futureListInterface.forEach(c -> interfaceReports.add((InterfaceReport) c.result()));
                packageReport.setInterfaceReports(interfaceReports);
            });

            List<Future> futureListClassInterface = Stream.concat(futureListClass.stream(), futureListInterface.stream()).collect(Collectors.toList());

            CompositeFuture.all(futureListClassInterface).onComplete(res -> {
                callback.accept(packageReport);
                promise.complete(packageReport);
            });
        });
    }

    @Override
    public Future<ProjectReport> analyzeProject(String srcProjectFolderName, Consumer<ProjectElem> callback) {
        return this.getVertx().executeBlocking(promise -> {
            // ci creiamo la struttura dati del project report
            ProjectReportImpl projectReport = new ProjectReportImpl();
            List<Future> futureListPackage = new ArrayList<>();
            List<PackageReport> packageReports = new ArrayList<>();

            // mi preparo il path da cui deve partire l'analisi
            SourceRoot sourceRoot = new SourceRoot(Paths.get(srcProjectFolderName)).setParserConfiguration(new ParserConfiguration());
            List<ParseResult<CompilationUnit>> parseResultList;

            try {
                parseResultList = sourceRoot.tryToParse("");
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            // mi prendo i vari package che compongono il progetto
            List<PackageDeclaration> allCus = parseResultList.stream()
                    .filter(r -> r.getResult().isPresent() && r.isSuccessful())
                    .map(r -> r.getResult().get())
                    .filter(c -> c.getPackageDeclaration().isPresent())
                    .map(c -> c.getPackageDeclaration().get())
                    .distinct()
                    .collect(Collectors.toList());

            // mi preparo una lista di future per i futuri package
            for (PackageDeclaration packageDeclaration : allCus) {
                futureListPackage.add(getPackageReport(packageDeclaration.getNameAsString(), callback));
            }

            // aspetto che tutte le future siano pronte e restituisco il project report
            CompositeFuture.all(futureListPackage).onComplete(res -> {
                futureListPackage.forEach(c -> packageReports.add((PackageReport) c.result()));
                projectReport.setPackageReports(packageReports);
                callback.accept(projectReport);
                promise.complete(projectReport);
            });
        });
    }

    private void log(String msg) {
        System.out.println("[REACTIVE AGENT] " + Thread.currentThread().getName() + msg);
    }

    private List<ParseResult<CompilationUnit>> createParsedFileList(PackageDeclaration dec) {
        SourceRoot sourceRoot = new SourceRoot(Paths.get("src/main/java/")).setParserConfiguration(new ParserConfiguration());
        List<ParseResult<CompilationUnit>> parseResultList;
        try {
            parseResultList = sourceRoot.tryToParse(dec.getNameAsString());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return parseResultList;
    }
}
