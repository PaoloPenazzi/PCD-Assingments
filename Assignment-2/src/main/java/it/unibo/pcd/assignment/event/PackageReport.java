package it.unibo.pcd.assignment.event;

import java.util.List;

public interface PackageReport {

	String getFullClassName();
	
	String getSrcFullFileName();

	List<MethodInfo> getMethodsInfo();

	List<FieldInfo> getFieldsInfo();
	
}
