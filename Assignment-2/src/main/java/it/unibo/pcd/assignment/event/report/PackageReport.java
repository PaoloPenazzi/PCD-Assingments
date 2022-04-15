package it.unibo.pcd.assignment.event.report;

import java.util.List;

public interface PackageReport {

	String getFullPackageName();
	
	String getSrcFullFileName();

	List<ClassReport> getClassesReport();

	List<InterfaceReport> getInterfacesReport();
	
}
