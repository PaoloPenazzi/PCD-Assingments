package it.unibo.pcd.assignment.event.report;

import it.unibo.pcd.assignment.event.ProjectElem;

import java.util.List;

public interface PackageReport extends ProjectElem {

    String getFullPackageName();

    List<ClassReport> getClassesReport();

    List<InterfaceReport> getInterfacesReport();

}
