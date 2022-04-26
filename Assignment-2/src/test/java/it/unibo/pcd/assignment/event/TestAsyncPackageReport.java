package it.unibo.pcd.assignment.event;

import io.vertx.core.Future;
import it.unibo.pcd.assignment.event.report.PackageReport;

public class TestAsyncPackageReport {

    public static void main(String[] args) {
        ProjectAnalyzer projectAnalyzer = new ProjectAnalyzerImpl();
        Future<PackageReport> reportFuture = projectAnalyzer.getPackageReport("it.unibo.pcd.assignment.event.report", System.out::println);
        System.out.println(" \n \n \n \n");
        reportFuture.onComplete(res -> System.out.println(res.result().toString()));
    }

}
