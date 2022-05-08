package it.unibo.pcd.assignment.reactive.model;

/**
 * A Reactive Java files analyzer.
 * The results of the analysis are available on the observables provided by the class.
 * All files must be under src/main/java.
 */
public interface ReactiveAnalyzer {
    /**
     * Perform a project analysis non-atomically.
     * Each file of the project is analysed incrementally.
     * This method is full reactive and can't be stopped at any time.
     * @param projectPath the project path.
     */
    void analyzeProject(String projectPath);

    /**
     * Analyze a project and return the report on the corresponding observable.
     * To analyze a project you need to select the java folder.
     * @param projectPath the project path.
     */
    void getProjectReport(String projectPath);

    /**
     * Analyze a package and return the report on the corresponding observable.
     * @param packagePath the package path.
     */
    void getPackageReport(String packagePath);

    /**
     * Analyze a single class and return the report on the corresponding observable.
     * @param classPath the class path.
     */
    void getClassReport(String classPath);

    /**
     * Analyze a single interface and return the report on the corresponding observable.
     * @param interfacePath the interface path.
     */
    void getInterfaceReport(String interfacePath);
}
