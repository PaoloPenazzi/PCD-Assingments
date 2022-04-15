package it.unibo.pcd.assignment.event.report;

import java.util.ArrayList;
import java.util.List;

public class ClassReportImpl implements ClassReport {
    private String fullClassName;
    private String srcFullFileName;
    private List<MethodInfo> methodsInfo;
    private List<FieldInfo> fieldsInfo;

    public void setFullClassName(String fullClassName) {
        this.fullClassName = fullClassName;
    }

    public void setSrcFullFileName(String srcFullFileName) {
        this.srcFullFileName = srcFullFileName;
    }

    public void setMethodsInfo(List<? extends MethodInfo> methodsInfo) {
        this.methodsInfo = new ArrayList<>(methodsInfo);
    }

    public void setFieldsInfo(List<? extends FieldInfo> fieldsInfo) {
        this.fieldsInfo = new ArrayList<>(fieldsInfo);
    }

    @Override
    public String getFullClassName() {
        return this.fullClassName;
    }

    @Override
    public String getSrcFullFileName() {
        return this.srcFullFileName;
    }

    @Override
    public List<MethodInfo> getMethodsInfo() {
        return this.methodsInfo;
    }

    @Override
    public List<FieldInfo> getFieldsInfo() {
        return this.fieldsInfo;
    }

    @Override
    public String toString() {
        return "ClassReportImpl{" +
                "fullClassName='" + fullClassName + '\'' +
                ", srcFullFileName='" + srcFullFileName + '\'' +
                ", methodsInfo=" + methodsInfo +
                ", fieldsInfo=" + fieldsInfo +
                '}';
    }
}
