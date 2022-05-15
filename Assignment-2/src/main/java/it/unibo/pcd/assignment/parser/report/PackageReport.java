package it.unibo.pcd.assignment.parser.report;

import it.unibo.pcd.assignment.parser.ProjectElem;

import java.util.List;

public interface PackageReport extends ProjectElem {

    String getFullPackageName();

    List<ClassReport> getClassesReport();

    List<InterfaceReport> getInterfacesReport();

}
