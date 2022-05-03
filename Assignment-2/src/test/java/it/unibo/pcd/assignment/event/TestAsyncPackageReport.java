package it.unibo.pcd.assignment.event;

import hu.webarticum.treeprinter.SimpleTreeNode;

public class TestAsyncPackageReport {
    public static void main(String[] args) {
        ProjectAnalyzer projectAnalyzer = new ProjectAnalyzerImpl();
        ProjectAnalyzerImpl.PATH = "./src/main/java";
        projectAnalyzer.getPackageReport("it.unibo.pcd.assignment.event.report",
                new SimpleTreeNode("Package Father Check")).onComplete(res -> System.out.println(res.result()));
    }
}
