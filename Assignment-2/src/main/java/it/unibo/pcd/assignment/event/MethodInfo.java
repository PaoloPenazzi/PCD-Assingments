package it.unibo.pcd.assignment.event;

public interface MethodInfo {

	boolean isMain();
	String getName();
	int getSrcBeginLine();
	int getEndBeginLine();
	ClassReport getParent();
		
}
