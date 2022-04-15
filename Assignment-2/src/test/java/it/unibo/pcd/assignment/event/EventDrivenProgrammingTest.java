package it.unibo.pcd.assignment.event;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import it.unibo.pcd.assignment.event.collector.ClassCollector;
import it.unibo.pcd.assignment.event.collector.InterfaceCollector;
import it.unibo.pcd.assignment.event.report.ClassReportImpl;
import it.unibo.pcd.assignment.event.report.InterfaceReportImpl;
import org.junit.Test;

import java.io.File;
import java.io.FileNotFoundException;

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
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
        InterfaceReportImpl interfaceReport = new InterfaceReportImpl();
        InterfaceCollector interfaceCollector = new InterfaceCollector();
        interfaceCollector.visit(cu, interfaceReport);
        System.out.println(interfaceReport);
    }

}
