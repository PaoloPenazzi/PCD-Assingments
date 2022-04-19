package it.unibo.pcd.assignment.event.report;

import java.util.List;

public interface ClassReport {
    String getFullClassName();

    String getSrcFullFileName();

    List<MethodInfo> getMethodsInfo();

    List<FieldInfo> getFieldsInfo();

}
