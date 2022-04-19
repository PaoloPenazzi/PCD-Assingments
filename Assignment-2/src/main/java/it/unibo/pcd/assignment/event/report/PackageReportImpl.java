package it.unibo.pcd.assignment.event.report;

import java.util.ArrayList;
import java.util.List;

public class PackageReportImpl implements PackageReport {
    private String fullPackageName;
    private List<ClassReportImpl> classReports;
    private List<InterfaceReportImpl> interfaceReports;

    public void setClassReports(List<ClassReportImpl> classReports) {
        this.classReports = classReports;
    }

    public void setInterfaceReports(List<InterfaceReportImpl> interfaceReports) {
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
        return new ArrayList<>(this.classReports);
    }

    @Override
    public List<InterfaceReport> getInterfacesReport() {
        return new ArrayList<>(this.interfaceReports);
    }

    @Override
    public String toString() {
        return "PackageReportImpl: " + fullPackageName + '\n' +
                "Classes: " + classReports.toString() + '\n' +
                "Interfaces: " + interfaceReports.toString() + '\n' + '\n';
    }
}
