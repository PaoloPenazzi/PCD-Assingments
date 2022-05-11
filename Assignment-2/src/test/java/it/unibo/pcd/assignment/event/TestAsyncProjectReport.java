package it.unibo.pcd.assignment.event;

public class TestAsyncProjectReport {
    public static void main(String[] args) {
        ProjectAnalyzerImpl projectAnalyzer = new ProjectAnalyzerImpl();
        projectAnalyzer.setPATH("src/main/java");
        projectAnalyzer.getProjectReport("src/main/java").onComplete(res -> projectAnalyzer.getViewController().log(res.result().toString()));
    }
}
