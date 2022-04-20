package it.unibo.pcd.assignment.event;

import io.vertx.core.Future;
import it.unibo.pcd.assignment.event.report.InterfaceReport;

public class TestAsyncInterfaceReport {

    public static void main(String[] args) {
        ProjectAnalyzer projectAnalyzer = new ProjectAnalyzerImpl();
        Future<InterfaceReport> reportFuture = projectAnalyzer.getInterfaceReport("src/main/java/it/unibo/pcd/assignment/event/ProjectAnalyzer.java");
        reportFuture.onComplete(res -> System.out.println(res.result().toString()));
    }

}
