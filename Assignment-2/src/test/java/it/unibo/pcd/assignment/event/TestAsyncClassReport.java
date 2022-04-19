package it.unibo.pcd.assignment.event;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import it.unibo.pcd.assignment.event.report.ClassReport;

class TestExecClass extends AbstractVerticle {

    public void start() {
        Future<ClassReport> reportFuture = new ProjectAnalyzerImpl(getVertx()).getClassReport("src/main/java/it/unibo/pcd/assignment/event/report/MethodInfoImpl.java");
        reportFuture.onComplete(res -> System.out.println(res.result().toString()));
    }
}

public class TestAsyncClassReport {

    public static void main(String[] args) {
        Vertx vertx = Vertx.vertx();
        vertx.deployVerticle(new TestExecClass());
    }

}
