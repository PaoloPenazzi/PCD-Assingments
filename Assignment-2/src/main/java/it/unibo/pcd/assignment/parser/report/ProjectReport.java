package it.unibo.pcd.assignment.parser.report;

import com.github.javaparser.utils.Pair;
import it.unibo.pcd.assignment.parser.ProjectElem;

import java.util.List;

public interface ProjectReport extends ProjectElem {

    List<PackageReport> getPackageReport();

    List<Pair<String, String>> getPackageAndMain();

    String getProjectName();
}
