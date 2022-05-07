package it.unibo.pcd.assignment.event.report;

import com.github.javaparser.utils.Pair;
import it.unibo.pcd.assignment.event.ProjectElem;

import java.util.ArrayList;
import java.util.List;

public class ProjectReportImpl implements ProjectReport, ProjectElem {
    private List<PackageReport> packageReports;
    private List<Pair<String, String>> pairList;

    public void setPackageReports(List<PackageReport> packageReports) {
        this.packageReports = packageReports;
    }

    // TODO NELLA PARTE ASYNC
    public void setPairList(List<Pair<String, String>> pairList) {
        this.pairList = pairList;
    }

    @Override
    public List<PackageReport> getPackageReport() {
        return this.packageReports;
    }

    // TODO NELLA PARTE ASYNC
    @Override
    public List<Pair<String, String>> getPackageAndMain() {
        return new ArrayList<>(this.pairList);
    }

    @Override
    public String toString() {
        return "ProjectReportImpl{" +
                "packageReports=" + packageReports.toString() +
                // ", pairList=" + pairList.toString() +
                '}';
    }
}
