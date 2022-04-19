package it.unibo.pcd.assignment.event;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.PackageDeclaration;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import it.unibo.pcd.assignment.event.collector.ClassCollector;
import it.unibo.pcd.assignment.event.collector.InterfaceCollector;
import it.unibo.pcd.assignment.event.collector.PackageCollector;
import it.unibo.pcd.assignment.event.collector.ProjectCollector;
import it.unibo.pcd.assignment.event.report.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.function.Consumer;

public class ProjectAnalyzerImpl implements ProjectAnalyzer {
    private final Vertx vertx;

    public ProjectAnalyzerImpl(Vertx vertx) {
        this.vertx = vertx;
    }

    @Override
    public Future<InterfaceReport> getInterfaceReport(String srcInterfacePath) {
        return this.vertx.executeBlocking(promise -> {
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
            promise.complete(interfaceReport);
        });
    }

    @Override
    public Future<ClassReport> getClassReport(String srcClassPath) {
        return this.vertx.executeBlocking(promise -> {
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
            promise.complete(classReport);
        });
    }

    @Override
    public Future<PackageReport> getPackageReport(String srcPackagePath) {
        return this.vertx.executeBlocking(promise -> {
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
        return this.vertx.executeBlocking(promise -> {
            this.log("Starting on getProjectReport");
            ProjectCollector projectCollector = new ProjectCollector();
            ProjectReportImpl projectReport = new ProjectReportImpl();
            projectCollector.visit(srcProjectFolderPath, projectReport);
            promise.complete(projectReport);
        });
    }

    @Override
    public void analyzeProject(String srcProjectFolderName, Consumer<ProjectElem> callback) {

    }

    private void log(String msg) {
        System.out.println("[REACTIVE AGENT] " + Thread.currentThread() + msg);
    }
}
