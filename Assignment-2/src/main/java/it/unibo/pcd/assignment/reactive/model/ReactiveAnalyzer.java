package it.unibo.pcd.assignment.reactive.model;

public interface ReactiveAnalyzer {
    void analyzeProject(String projectPath);

    void analyzePackage(String packagePath);
}
