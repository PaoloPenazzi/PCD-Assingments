package it.unibo.pcd.assignment.event;

import java.util.List;

// sara il nsotro collector
public interface ClassReport {

	//aggiungere setter per sta merda di info

	String getFullClassName();
	
	String getSrcFullFileName();

	List<MethodInfo> getMethodsInfo();

	List<FieldInfo> getFieldsInfo();
	
}
