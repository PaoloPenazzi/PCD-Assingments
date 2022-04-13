package it.unibo.pcd.assignment.event;

public class MethodInfoImpl implements MethodInfo{
    private String name;
    private int beginLine;
    private int endBeginLine;
    private ClassReport parentClass;

    public void setName(String name) {
        this.name = name;
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
}
