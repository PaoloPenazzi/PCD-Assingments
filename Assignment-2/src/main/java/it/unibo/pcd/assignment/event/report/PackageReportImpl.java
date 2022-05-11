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
        if(this.classReports == null){
            return "Package Name: " + fullPackageName + "\n\n";
        } else {
            return "PackageReportImpl: " + fullPackageName + "\n\n" +
                    "Classes: " + classReports + '\n' +
                    "Interfaces: " + interfaceReports + '\n' + '\n';
        }
    }
}
