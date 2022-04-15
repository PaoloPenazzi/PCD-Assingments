package it.unibo.pcd.assignment.event.report;

import it.unibo.pcd.assignment.event.report.ClassReport;

public interface FieldInfo {
	String getName();
	String getFieldTypeFullName();
	ClassReport getParent();
}
