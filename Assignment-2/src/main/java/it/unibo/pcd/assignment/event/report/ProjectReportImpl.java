package it.unibo.pcd.assignment.event.report;

import com.github.javaparser.utils.Pair;
import it.unibo.pcd.assignment.event.ProjectElem;

import java.util.ArrayList;
import java.util.List;

public class ProjectReportImpl implements ProjectReport, ProjectElem {
    private List<PackageReport> packageReports;
    private List<Pair<String, String>> pairList;
    private String projectName;

    public void setPackageReports(List<PackageReport> packageReports) {
        this.packageReports = packageReports;
    }

    public void setPairList(List<Pair<String, String>> pairList) {
        this.pairList = pairList;
    }

    @Override
    public List<PackageReport> getPackageReport() {
        return this.packageReports;
    }

    @Override
    public List<Pair<String, String>> getPackageAndMain() {
        return new ArrayList<>(this.pairList);
    }

    @Override
    public String getProjectName() {
        return this.projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    private String createReport(){
        StringBuilder report = new StringBuilder();
        for (PackageReport packageReport : getPackageReport()) {
            report.append("Package: ").append(packageReport.getFullPackageName());
            report.append("\n\n");
            for (ClassReport classReport : packageReport.getClassesReport()) {
                report.append(classReport.toString());
                report.append("\n");
            }
            for (InterfaceReport interfaceReport : packageReport.getInterfacesReport()) {
                report.append(interfaceReport.toString());
                report.append("\n\n");
            }
            report.append("\n");
        }
        return report.toString();
    }

    @Override
    public String toString() {
        return "Project Report\n\n" + createReport() + "";
    }
}
