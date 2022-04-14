package it.unibo.pcd.assignment.event;

public class MethodInfoImpl implements MethodInfo {
    private String name;

    private String modifiers;

    private int beginLine;

    private int endBeginLine;

    private ClassReport parentClass;
    public void setName(String name) {
        this.name = name;
    }

    public void setModifiers(String modifiers) {
        this.modifiers = modifiers;
    }

    public void setBeginLine(int beginLine) {
        this.beginLine = beginLine;
    }

    public void setEndBeginLine(int endBeginLine) {
        this.endBeginLine = endBeginLine;
    }

    public void setParentClass(ClassReport parentClass) {
        this.parentClass = parentClass;
    }

    @Override
    public String getName() {
        return this.name;
    }

    public String getModifiers() {
        return modifiers;
    }

    @Override
    public int getSrcBeginLine() {
        return this.beginLine;
    }

    @Override
    public int getEndBeginLine() {
        return this.endBeginLine;
    }

    @Override
    public ClassReport getParent() {
        return this.parentClass;
    }

    @Override
    public String toString() {
        return "MethodInfoImpl{" +
                "name='" + name + '\'' +
                ", modifiers='" + modifiers + '\'' +
                ", beginLine=" + beginLine +
                ", endBeginLine=" + endBeginLine +
                ", parentClass=" + parentClass.getFullClassName() +
                '}';
    }
}
