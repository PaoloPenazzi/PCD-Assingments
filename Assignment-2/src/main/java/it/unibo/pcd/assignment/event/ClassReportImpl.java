package it.unibo.pcd.assignment.event;

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

    public void setMethodsInfo(List<MethodInfo> methodsInfo) {
        this.methodsInfo = methodsInfo;
    }

    public void setFieldsInfo(List<FieldInfo> fieldsInfo) {
        this.fieldsInfo = fieldsInfo;
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
}
