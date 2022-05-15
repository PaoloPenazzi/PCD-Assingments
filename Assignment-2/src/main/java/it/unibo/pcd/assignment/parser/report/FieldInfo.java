package it.unibo.pcd.assignment.parser.report;

public interface FieldInfo {
    String getName();

    String getFieldTypeFullName();

    ClassReport getParent();
}
