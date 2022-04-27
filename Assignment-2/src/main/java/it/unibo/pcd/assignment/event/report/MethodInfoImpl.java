package it.unibo.pcd.assignment.event.report;

public class MethodInfoImpl implements MethodInfo {
    private String name;
    private String modifiers;
    private int beginLine;
    private int endBeginLine;
    private boolean isMain;
    private ClassReport parentClass;

    public void setBeginLine(int beginLine) {
        this.beginLine = beginLine;
    }

    public void setParentClass(ClassReport parentClass) {
        this.parentClass = parentClass;
    }

    @Override
    public boolean isMain() {
        return this.isMain;
    }

    public void setMain(boolean main) {
        this.isMain = main;
    }

    @Override
    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getModifiers() {
        return modifiers;
    }

    public void setModifiers(String modifiers) {
        this.modifiers = modifiers;
    }

    @Override
    public int getSrcBeginLine() {
        return this.beginLine;
    }

    @Override
    public int getEndBeginLine() {
        return this.endBeginLine;
    }

    public void setEndBeginLine(int endBeginLine) {
        this.endBeginLine = endBeginLine;
    }

    @Override
    public ClassReport getParent() {
        return this.parentClass;
    }

    @Override
    public String toString() {
        return this.name;
    }

    class Test1 {
        int zero = 0;

        int metodo() {
            return 1;
        }

        class Test2 {
            int zero1 = 1;

            int metodo1() {
                return 2;
            }
        }
    }

    class Test3 {
        int numero = 2;

        int metodo3() {
            return 3;
        }
    }

}
