package it.unibo.pcd.assignment.event.report;

import it.unibo.pcd.assignment.event.report.ClassReport;

public interface MethodInfo {

	boolean isMain();
	String getName();
	int getSrcBeginLine();
	int getEndBeginLine();
	ClassReport getParent();
		
}
