package it.unibo.pcd.assignment.event.report;

import com.github.javaparser.utils.Pair;
import it.unibo.pcd.assignment.event.ProjectElem;

import java.util.List;

public interface ProjectReport extends ProjectElem {

    List<PackageReport> getPackageReport();

    List<Pair<String, String>> getPackageAndMain();
}
