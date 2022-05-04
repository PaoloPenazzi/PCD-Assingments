package it.unibo.pcd.assignment.event;

public class TestAsyncProjectReport {
    public static void main(String[] args) {
        ProjectAnalyzer projectAnalyzer = new ProjectAnalyzerImpl();
        ProjectAnalyzerImpl.PATH = "src/main/java";
        projectAnalyzer.getProjectReport("src/main/java");
    }
}
