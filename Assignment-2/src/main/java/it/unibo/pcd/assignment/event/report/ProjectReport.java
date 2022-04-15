package it.unibo.pcd.assignment.event.report;

import java.util.List;

public interface ProjectReport {

	ClassReport getMainClass();
	
	List<ClassReport> getAllClasses();
	
	ClassReport getClassReport(String fullClassName);
}
