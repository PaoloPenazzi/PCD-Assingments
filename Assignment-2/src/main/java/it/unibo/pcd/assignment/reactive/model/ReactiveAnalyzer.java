package it.unibo.pcd.assignment.reactive.model;

public interface ReactiveAnalyzer {
    void analyzeProject(String projectPath);
    void getProjectReport(String projectPath);
    void getPackageReport(String packagePath);
    void getClassReport(String classPath);
    void getInterfaceReport(String interfacePath);
}
