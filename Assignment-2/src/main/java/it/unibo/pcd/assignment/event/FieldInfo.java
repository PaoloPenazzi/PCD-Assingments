package it.unibo.pcd.assignment.event;

public interface FieldInfo {

	String getName();
	String getFieldTypeFullName();
	
	ClassReport getParent();		
}
