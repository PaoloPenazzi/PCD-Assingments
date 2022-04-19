package it.unibo.pcd.assignment.event;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.PackageDeclaration;
import it.unibo.pcd.assignment.event.collector.ClassCollector;
import it.unibo.pcd.assignment.event.collector.InterfaceCollector;
import it.unibo.pcd.assignment.event.collector.PackageCollector;
import it.unibo.pcd.assignment.event.report.ClassReportImpl;
import it.unibo.pcd.assignment.event.report.InterfaceReportImpl;
import it.unibo.pcd.assignment.event.report.PackageReportImpl;
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

}
