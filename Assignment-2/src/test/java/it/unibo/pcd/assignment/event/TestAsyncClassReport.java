package it.unibo.pcd.assignment.event;

import io.vertx.core.Future;
import it.unibo.pcd.assignment.event.report.ClassReport;

public class TestAsyncClassReport {

    public static void main(String[] args) {
        ProjectAnalyzer projectAnalyzer = new ProjectAnalyzerImpl();
        Future<ClassReport> reportFuture = projectAnalyzer.
                getClassReport("src/main/java/it/unibo/pcd/assignment/event/report/MethodInfoImpl.java", System.out::println);
        System.out.println(" \n \n \n \n");
        reportFuture.onComplete(res -> System.out.println(res.result()));
    }

}
