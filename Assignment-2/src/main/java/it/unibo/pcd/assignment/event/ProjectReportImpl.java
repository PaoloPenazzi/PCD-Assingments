package it.unibo.pcd.assignment.event;

import java.util.List;
import java.util.Objects;

public class ProjectReportImpl implements ProjectReport {
    private ClassReport mainClass;
    private List<ClassReport> allClasses;
    private ClassReport specificClassReport;

    public void setMainClass(ClassReport mainClass) {
        this.mainClass = mainClass;
    }

    public void setAllClasses(List<ClassReport> allClasses) {
        this.allClasses = allClasses;
    }

    public void setSpecificClassReport(ClassReport specificClassReport) {
        this.specificClassReport = specificClassReport;
    }

    @Override
    public ClassReport getMainClass() {
        return null;
    }

    @Override
    public List<ClassReport> getAllClasses() {
        return null;
    }

    @Override
    public ClassReport getClassReport(String fullClassName) {
        return null;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ProjectReportImpl that = (ProjectReportImpl) o;
        return Objects.equals(mainClass, that.mainClass) && Objects.equals(allClasses, that.allClasses) && Objects.equals(specificClassReport, that.specificClassReport);
    }

    @Override
    public int hashCode() {
        return Objects.hash(mainClass, allClasses, specificClassReport);
    }

    @Override
    public String toString() {
        return "ProjectReportImpl{" +
                "mainClass=" + mainClass.getFullClassName() +
                ", allClasses=" + allClasses.toString() +
                ", specificClassReport=" + specificClassReport.getFullClassName() +
                '}';
    }
}
