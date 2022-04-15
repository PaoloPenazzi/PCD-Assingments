package it.unibo.pcd.assignment.event.report;

import java.util.ArrayList;
import java.util.List;

public class PackageReportImpl implements PackageReport{
    private String fullPackageName;
    private String srcFullFileName;
    private List<ClassReportImpl> classReports;
    private List<InterfaceReportImpl> interfaceReports;

    public void setFullPackageName(String fullPackageName) {
        this.fullPackageName = fullPackageName;
    }

    public void setSrcFullFileName(String srcFullFileName) {
        this.srcFullFileName = srcFullFileName;
    }

    public void setClassReports(List<ClassReportImpl> classReports) {
        this.classReports = classReports;
    }

    public void setInterfaceReports(List<InterfaceReportImpl> interfaceReports) {
        this.interfaceReports = interfaceReports;
    }

    public String getFullPackageName() {
        return this.fullPackageName;
    }

    @Override
    public String getSrcFullFileName() {
        return this.srcFullFileName;
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
        return "PackageReportImpl{" +
                "fullPackageName='" + fullPackageName + '\'' +
                ", srcFullFileName='" + srcFullFileName + '\'' +
                ", classReports=" + classReports.toString() +
                ", interfaceReports=" + interfaceReports.toString() +
                '}';
    }
}
