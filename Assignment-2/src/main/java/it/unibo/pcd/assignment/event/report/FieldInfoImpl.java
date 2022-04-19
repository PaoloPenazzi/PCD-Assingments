package it.unibo.pcd.assignment.event.report;

public class FieldInfoImpl implements FieldInfo {
    private String name;
    private String type;
    private ClassReport parentClass;

    @Override
    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String getFieldTypeFullName() {
        return this.type;
    }

    @Override
    public ClassReport getParent() {
        return this.parentClass;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setParentClass(ClassReport parentClass) {
        this.parentClass = parentClass;
    }

    @Override
    public String toString() {
        return "FieldInfoImpl{" +
                "name='" + name + '\'' +
                ", type='" + type + '\'' +
                ", parentClass=" + parentClass.getFullClassName() +
                '}';
    }
}
