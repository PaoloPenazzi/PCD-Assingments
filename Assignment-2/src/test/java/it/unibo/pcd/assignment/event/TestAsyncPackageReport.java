package it.unibo.pcd.assignment.event;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import it.unibo.pcd.assignment.event.report.PackageReport;

class TestExecPackage extends AbstractVerticle {

    public void start() {
        Future<PackageReport> reportFuture = new ProjectAnalyzerImpl(getVertx()).getPackageReport("it.unibo.pcd.assignment.event.report");
        reportFuture.onComplete(res -> System.out.println(res.result().toString()));
    }
}

public class TestAsyncPackageReport {

    public static void main(String[] args) {
        Vertx vertx = Vertx.vertx();
        vertx.deployVerticle(new TestExecPackage());
    }

}
