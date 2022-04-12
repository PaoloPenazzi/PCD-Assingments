package it.unibo.pcd.assignment.event;

public interface MethodInfo {

	String getName();
	int getSrcBeginLine();
	int getEndBeginLine();
	ClassReport getParent();
		
}
