package it.unibo.pcd.assignment.event;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import it.unibo.pcd.assignment.event.report.InterfaceReport;

class TestExecInterface extends AbstractVerticle {

    public void start() {
        Future<InterfaceReport> reportFuture = new ProjectAnalyzerImpl(getVertx()).getInterfaceReport("src/main/java/it/unibo/pcd/assignment/event/ProjectAnalyzer.java");
        reportFuture.onComplete(res -> System.out.println(res.result().toString()));
    }
}

public class TestAsyncInterfaceReport {

    public static void main(String[] args) {
        Vertx vertx = Vertx.vertx();
        vertx.deployVerticle(new TestExecInterface());
    }

}
