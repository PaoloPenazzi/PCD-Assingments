package it.unibo.pcd.assignment.event;

import io.vertx.core.Future;
import it.unibo.pcd.assignment.event.report.ClassReport;
import it.unibo.pcd.assignment.event.report.InterfaceReport;
import it.unibo.pcd.assignment.event.report.PackageReport;
import it.unibo.pcd.assignment.event.report.ProjectReport;

import java.util.function.Consumer;

public class ProjectAnalyzerImpl implements ProjectAnalyzer {


    @Override
    public Future<InterfaceReport> getInterfaceReport(String srcInterfacePath) {
        return null;
    }

    @Override
    public Future<ClassReport> getClassReport(String srcClassPath) {
        return null;
    }

    @Override
    public Future<PackageReport> getPackageReport(String srcPackagePath) {
        return null;
    }

    @Override
    public Future<ProjectReport> getProjectReport(String srcProjectFolderPath) {
        return null;
    }

    @Override
    public void analyzeProject(String srcProjectFolderName, Consumer<ProjectElem> callback) {

    }
}
