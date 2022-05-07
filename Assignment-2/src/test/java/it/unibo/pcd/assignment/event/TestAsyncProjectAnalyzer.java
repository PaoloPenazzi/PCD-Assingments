package it.unibo.pcd.assignment.event;

public class TestAsyncProjectAnalyzer {

    public static void main(String[] args) {
        ProjectAnalyzerImpl projectAnalyzer = new ProjectAnalyzerImpl();
        projectAnalyzer.setPATH("src/main/java");
        projectAnalyzer.analyzeProject("src/main/java", System.out::println);
    }

}
