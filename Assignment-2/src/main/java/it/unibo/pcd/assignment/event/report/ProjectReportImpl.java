package it.unibo.pcd.assignment.event.report;

import com.github.javaparser.utils.Pair;

import java.util.ArrayList;
import java.util.List;

public class ProjectReportImpl implements ProjectReport {
    private List<PackageReportImpl> packageReports;
    private List<Pair<String, String>> pairList;

    public void setPackageReports(List<PackageReportImpl> packageReports) {
        this.packageReports = packageReports;
    }

    public void setPairList(List<Pair<String, String>> pairList) {
        this.pairList = pairList;
    }

    @Override
    public List<PackageReport> getPackageReport() {
        return new ArrayList<>(this.packageReports);
    }

    @Override
    public List<Pair<String, String>> getPackageAndMain() {
        return new ArrayList<>(this.pairList);
    }

    @Override
    public String toString() {
        return "ProjectReportImpl{" +
                "packageReports=" + packageReports.toString() +
                ", pairList=" + pairList.toString() +
                '}';
    }
}
