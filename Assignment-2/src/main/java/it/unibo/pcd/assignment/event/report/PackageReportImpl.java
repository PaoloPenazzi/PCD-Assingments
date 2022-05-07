package it.unibo.pcd.assignment.event.report;

import it.unibo.pcd.assignment.event.ProjectElem;

import java.util.List;

public class PackageReportImpl implements PackageReport, ProjectElem {
    private String fullPackageName;
    private List<ClassReport> classReports;
    private List<InterfaceReport> interfaceReports;

    public void setClassReports(List<ClassReport> classReports) {
        this.classReports = classReports;
    }

    public void setInterfaceReports(List<InterfaceReport> interfaceReports) {
        this.interfaceReports = interfaceReports;
    }

    public String getFullPackageName() {
        return this.fullPackageName;
    }

    public void setFullPackageName(String fullPackageName) {
        this.fullPackageName = fullPackageName;
    }

    @Override
    public List<ClassReport> getClassesReport() {
        return this.classReports;
    }

    @Override
    public List<InterfaceReport> getInterfacesReport() {
        return this.interfaceReports;
    }

    @Override
    public String toString() {
        return "PackageReportImpl: " + fullPackageName + '\n' +
                "Classes: " + classReports.toString() + '\n' +
                "Interfaces: " + interfaceReports.toString() + '\n' + '\n';
    }
}
