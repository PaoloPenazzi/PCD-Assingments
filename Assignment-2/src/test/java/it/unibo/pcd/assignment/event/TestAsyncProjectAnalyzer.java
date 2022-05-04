package it.unibo.pcd.assignment.event;

public class TestAsyncProjectAnalyzer {

    public static void main(String[] args) {
        ProjectAnalyzer projectAnalyzer = new ProjectAnalyzerImpl();
        projectAnalyzer.analyzeProject("src/main/java", System.out::println);
    }

}
