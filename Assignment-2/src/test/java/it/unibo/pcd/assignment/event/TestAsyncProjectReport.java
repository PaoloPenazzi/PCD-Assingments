package it.unibo.pcd.assignment.event;

import io.vertx.core.Future;
import it.unibo.pcd.assignment.event.report.ProjectReport;

public class TestAsyncProjectReport {

    public static void main(String[] args) {
        ProjectAnalyzer projectAnalyzer = new ProjectAnalyzerImpl();
        Future<ProjectReport> reportFuture = projectAnalyzer.getProjectReport("src/main/java");
        reportFuture.onComplete(res -> System.out.println(res.result().toString()));
    }

}
