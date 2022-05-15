package it.unibo.pcd.assignment.parser.report;

import it.unibo.pcd.assignment.parser.ProjectElem;

import java.util.List;

public interface ClassReport extends ProjectElem {
    String getFullClassName();

    String getSrcFullFileName();

    List<MethodInfo> getMethodsInfo();

    List<FieldInfo> getFieldsInfo();

    ClassReportImpl getInnerClass();

}
