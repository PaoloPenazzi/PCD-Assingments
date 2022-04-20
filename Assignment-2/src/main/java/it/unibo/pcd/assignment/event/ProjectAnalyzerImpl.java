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
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import it.unibo.pcd.assignment.event.collector.ClassCollector;
import it.unibo.pcd.assignment.event.collector.InterfaceCollector;
import it.unibo.pcd.assignment.event.collector.PackageCollector;
import it.unibo.pcd.assignment.event.collector.ProjectCollector;
import it.unibo.pcd.assignment.event.report.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class ProjectAnalyzerImpl extends AbstractVerticle implements ProjectAnalyzer {

    public ProjectAnalyzerImpl() {
        Vertx.vertx().deployVerticle(this);
    }

    @Override
    public Future<InterfaceReport> getInterfaceReport(String srcInterfacePath) {
        return this.getVertx().executeBlocking(promise -> {
            this.log("Starting on getInterfaceReport");
            CompilationUnit compilationUnit;
            try {
                compilationUnit = StaticJavaParser.parse(new File(srcInterfacePath));
            } catch (FileNotFoundException e) {
                throw new RuntimeException(e);
            }
            InterfaceReportImpl interfaceReport = new InterfaceReportImpl();
            InterfaceCollector interfaceCollector = new InterfaceCollector();
            interfaceCollector.visit(compilationUnit, interfaceReport);
            System.out.println(interfaceReport);
            promise.complete(interfaceReport);
        });
    }

    @Override
    public Future<ClassReport> getClassReport(String srcClassPath) {
        return this.getVertx().executeBlocking(promise -> {
            this.log("Starting on getClassReport");
            CompilationUnit compilationUnit;
            try {
                compilationUnit = StaticJavaParser.parse(new File(srcClassPath));
            } catch (FileNotFoundException e) {
                throw new RuntimeException(e);
            }
            ClassReportImpl classReport = new ClassReportImpl();
            ClassCollector classCollector = new ClassCollector();
            classCollector.visit(compilationUnit, classReport);
            System.out.println(classReport);
            promise.complete(classReport);
        });
    }

    @Override
    public Future<PackageReport> getPackageReport(String srcPackagePath) {
        return this.getVertx().executeBlocking(promise -> {
            this.log("Starting on getPackageReport");
            PackageDeclaration packageDeclaration;
            packageDeclaration = StaticJavaParser.parsePackageDeclaration("package " + srcPackagePath + ";");
            PackageReportImpl packageReport = new PackageReportImpl();
            PackageCollector packageCollector = new PackageCollector();
            packageCollector.visit(packageDeclaration, packageReport);
            promise.complete(packageReport);
        });
    }

    @Override
    public Future<ProjectReport> getProjectReport(String srcProjectFolderPath) {
        return this.getVertx().executeBlocking(promise -> {
            this.log("Starting on getProjectReport");
            ProjectCollector projectCollector = new ProjectCollector();
            ProjectReportImpl projectReport = new ProjectReportImpl();
            projectCollector.visit(srcProjectFolderPath, projectReport);
            promise.complete(projectReport);
        });
    }

    @Override
    public void analyzeProject(String srcProjectFolderName, Consumer<ProjectElem> callback) {
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

        for (PackageDeclaration packageDeclaration : allCus) {
            // visito il package e per ciascuna classe/interfaccia...
            System.out.println("AAAAAAAAAAAAAAAAAAAAAA"+packageDeclaration.getNameAsString());

            // classes/interfaces report
            List<CompilationUnit> classesOrInterfacesUnit = this.createParsedFileList(packageDeclaration)
                    .stream()
                    .filter(r -> r.getResult().isPresent() && r.isSuccessful())
                    .map(r -> r.getResult().get())
                    .collect(Collectors.toList());

            for (CompilationUnit cu : classesOrInterfacesUnit) {

                List<ClassOrInterfaceDeclaration> declarationList = cu.getTypes().stream()
                        .map(TypeDeclaration::asTypeDeclaration)
                        .filter(BodyDeclaration::isClassOrInterfaceDeclaration)
                        .map(x -> (ClassOrInterfaceDeclaration) x)
                        .collect(Collectors.toList());

                for (ClassOrInterfaceDeclaration declaration : declarationList) {
                    if (declaration.isInterface()) {
                        this.getInterfaceReport("src/main/java/"+declaration.getFullyQualifiedName().get().replace(".", "/")+".java").onComplete(e -> {
                                    if(e.succeeded()){
                                        System.out.println(e.result().toString());
                                        callback.accept(e.result());
                                    }
                                }
                        );
                    } else {
                        this.getClassReport("src/main/java/"+declaration.getFullyQualifiedName().get().replace(".", "/")+".java").onComplete(e -> {
                                    if(e.succeeded()){
                                        System.out.println(e.result().toString());
                                        callback.accept(e.result());
                                    }
                                }
                        );
                    }
                }
            }
        }

    }

    private void log(String msg) {
        System.out.println("[REACTIVE AGENT] " + Thread.currentThread() + msg);
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
