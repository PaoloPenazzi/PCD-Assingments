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

    private String createReport(){
        StringBuilder report = new StringBuilder();
            for (ClassReport classReport : classReports) {
                report.append(classReport.toString());
                report.append("\n");
            }
            for (InterfaceReport interfaceReport : interfaceReports) {
                report.append(interfaceReport.toString());
                report.append("\n\n");
            }
            report.append("\n");
        return report.toString();
    }

    @Override
    public String toString() {
        if(this.classReports == null){
            return "Package: " + fullPackageName + "\n";
        } else {
            return "Package: " + fullPackageName + "\n\n" + createReport() + "\n\n";
        }
    }
}
