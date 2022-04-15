package it.unibo.pcd.assignment.event.report;

import java.util.List;

// sara il nsotro collector
public interface ClassReport {
	String getFullClassName();
	
	String getSrcFullFileName();

	List<MethodInfo> getMethodsInfo();

	List<FieldInfo> getFieldsInfo();
	
}
