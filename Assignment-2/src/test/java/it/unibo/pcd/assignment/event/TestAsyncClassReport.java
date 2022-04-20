package it.unibo.pcd.assignment.event;

import io.vertx.core.Future;
import it.unibo.pcd.assignment.event.report.ClassReport;

public class TestAsyncClassReport {

    public static void main(String[] args) {
        ProjectAnalyzer projectAnalyzer = new ProjectAnalyzerImpl();
        System.out.println(projectAnalyzer);
        Future<ClassReport> reportFuture = projectAnalyzer.getClassReport("src/main/java/it/unibo/pcd/assignment/event/report/MethodInfoImpl.java");
        reportFuture.onComplete(res -> System.out.println(res.result().toString()));
    }

}
