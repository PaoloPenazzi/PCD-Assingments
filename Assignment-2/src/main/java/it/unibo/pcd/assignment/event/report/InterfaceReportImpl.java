package it.unibo.pcd.assignment.event.report;

import java.util.List;

public class InterfaceReportImpl implements InterfaceReport {
    private String interfaceName;

    private String fullFileName;

    private List<String> methodNameList;

    @Override
    public String getFullInterfaceName() {
        return this.interfaceName;
    }

    @Override
    public String getSrcFullFileName() {
        return this.fullFileName;
    }

    @Override
    public List<String> getAllMethodsName() {
        return this.methodNameList;
    }

    public void setInterfaceName(String interfaceName) {
        this.interfaceName = interfaceName;
    }

    public void setFullFileName(String fullFileName) {
        this.fullFileName = fullFileName;
    }

    public void setMethodNameList(List<String> methodNameList) {
        this.methodNameList = methodNameList;
    }

    @Override
    public String toString() {
        return "InterfaceReportImpl: " + interfaceName + '\n' +
                "File Name: " + fullFileName + '\n' +
                "Methods: " + methodNameList + '\n' + '\n';
    }
}
