package it.unibo.pcd.assignment.event.report;

public interface FieldInfo {
    String getName();

    String getFieldTypeFullName();

    ClassReport getParent();
}
