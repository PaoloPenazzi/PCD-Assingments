package it.unibo.pcd.assignment.event;

public class TestAsyncInterfaceReport {

    public static void main(String[] args) {
        ProjectAnalyzer projectAnalyzer = new ProjectAnalyzerImpl();
        projectAnalyzer.getInterfaceReport("src/main/java/" +
                        "it/unibo/pcd/assignment/event/ProjectAnalyzer.java", null).onComplete(res -> System.out.println(res.result()));
    }

}
