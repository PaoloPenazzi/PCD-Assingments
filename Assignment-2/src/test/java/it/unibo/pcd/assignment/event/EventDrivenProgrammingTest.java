package it.unibo.pcd.assignment.event;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.PackageDeclaration;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import it.unibo.pcd.assignment.event.collector.ClassCollector;
import it.unibo.pcd.assignment.event.collector.InterfaceCollector;
import it.unibo.pcd.assignment.event.collector.PackageCollector;
import it.unibo.pcd.assignment.event.collector.ProjectCollector;
import it.unibo.pcd.assignment.event.report.*;
import org.junit.Test;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

public class EventDrivenProgrammingTest {

    @Test public void testClassReport() {
        CompilationUnit cu;
        try {
            cu = StaticJavaParser.parse(new File("src/main/java/it/unibo/pcd/assignment/event/TestClassReversePolishNotation.java"));
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
        ClassReportImpl classReport = new ClassReportImpl();
        ClassCollector classCollector = new ClassCollector();
        classCollector.visit(cu, classReport);
        System.out.println(classReport);
    }

    @Test public void testInterfaceReport() {
        CompilationUnit cu;
        try {

            cu = StaticJavaParser.parse(new File("src/main/java/it/unibo/pcd/assignment/event/ProjectReport.java"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        InterfaceReportImpl interfaceReport = new InterfaceReportImpl();
        InterfaceCollector interfaceCollector = new InterfaceCollector();
        interfaceCollector.visit(cu, interfaceReport);
        System.out.println(interfaceReport);
    }

    @Test public void testPackageReport() {
        PackageDeclaration cu;
        cu = StaticJavaParser.parsePackageDeclaration("package it.unibo.pcd.assignment.event.report;");
        // cu = StaticJavaParser.parseResource("src/main/java/it/unibo/pcd/assignment/event/");
        PackageReportImpl packageReport = new PackageReportImpl();
        PackageCollector packageCollector = new PackageCollector();
        packageCollector.visit(cu, packageReport);
        System.out.println(packageReport);
    }

    @Test public void testProjectReport() {
        ProjectCollector projectCollector = new ProjectCollector();
        ProjectReportImpl projectReport = new ProjectReportImpl();
        projectCollector.visit(projectReport);
        System.out.println("Package and Main: ");
        System.out.println(projectReport.getPackageAndMain());
        System.out.println();
        System.out.println();
        System.out.println("Packages report: ");
        System.out.println(projectReport.getPackageReport());
    }

    @Test
    public void testAsyncClassReport() {
        Vertx vertx = Vertx.vertx();
        vertx.deployVerticle("Oppla");
        ProjectAnalyzer projectAnalyzer = new ProjectAnalyzerImpl(vertx);
        Future<ClassReport> future =
                projectAnalyzer.getClassReport("src/main/java/it/unibo/pcd/assignment/event/report/MethodInfoImpl.java");
        future.onComplete((AsyncResult<ClassReport> promise) -> {
            System.out.println(promise.result());
        });
    }

    @Test
    public void testAsyncInterfaceReport() {}

    @Test
    public void testAsyncPackageReport() {}

    @Test
    public void testAsyncProjectReport() {}

}
