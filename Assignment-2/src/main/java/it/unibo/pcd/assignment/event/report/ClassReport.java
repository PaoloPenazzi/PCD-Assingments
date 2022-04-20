package it.unibo.pcd.assignment.event.report;

import it.unibo.pcd.assignment.event.ProjectElem;

import java.util.List;

public interface ClassReport extends ProjectElem {
    String getFullClassName();

    String getSrcFullFileName();

    List<MethodInfo> getMethodsInfo();

    List<FieldInfo> getFieldsInfo();

}
