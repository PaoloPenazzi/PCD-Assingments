package it.unibo.pcd.assignment.event;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import it.unibo.pcd.assignment.event.report.ProjectReport;

class TestExecProject extends AbstractVerticle {

    public void start() {
        Future<ProjectReport> reportFuture = new ProjectAnalyzerImpl(getVertx()).getProjectReport("src/main/java");
        reportFuture.onComplete(res -> System.out.println(res.result().toString()));
    }
}

public class TestAsyncProjectReport {

    public static void main(String[] args) {
        Vertx vertx = Vertx.vertx();
        vertx.deployVerticle(new TestExecProject());
    }

}
