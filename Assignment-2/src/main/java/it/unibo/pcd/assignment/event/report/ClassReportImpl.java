package it.unibo.pcd.assignment.event.report;

import it.unibo.pcd.assignment.event.ProjectElem;

import java.util.ArrayList;
import java.util.List;

public class ClassReportImpl implements ClassReport, ProjectElem {
    private String fullClassName;
    private String srcFullFileName;
    private List<MethodInfo> methodsInfo;
    private List<FieldInfo> fieldsInfo;
    private ClassReportImpl innerClass;

    @Override
    public String getFullClassName() {
        return this.fullClassName;
    }

    public void setFullClassName(String fullClassName) {
        this.fullClassName = fullClassName;
    }

    @Override
    public String getSrcFullFileName() {
        return this.srcFullFileName;
    }

    public void setSrcFullFileName(String srcFullFileName) {
        this.srcFullFileName = srcFullFileName;
    }

    @Override
    public List<MethodInfo> getMethodsInfo() {
        return this.methodsInfo;
    }

    public void setMethodsInfo(List<? extends MethodInfo> methodsInfo) {
        this.methodsInfo = new ArrayList<>(methodsInfo);
    }

    @Override
    public List<FieldInfo> getFieldsInfo() {
        return this.fieldsInfo;
    }

    public void setFieldsInfo(List<? extends FieldInfo> fieldsInfo) {
        this.fieldsInfo = new ArrayList<>(fieldsInfo);
    }

    @Override
    public ClassReportImpl getInnerClass() {
        return this.innerClass;
    }

    public void setInnerClass(ClassReportImpl newInnerClass) {
        this.innerClass = newInnerClass;
    }

    @Override
    public String toString() {
        return "Class: " + fullClassName + "\n" +
                "Full Name: " + srcFullFileName + "\n" +
                "Fields: " + fieldsInfo.toString() + "\n" +
                "Methods: " + methodsInfo.toString() + "\n" +
                (innerClass == null ? "" : innerClass.toString());
    }
}
