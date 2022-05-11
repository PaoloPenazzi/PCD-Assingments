package it.unibo.pcd.assignment.event.collector;

import com.github.javaparser.Position;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import it.unibo.pcd.assignment.event.report.ClassReportImpl;
import it.unibo.pcd.assignment.event.report.FieldInfoImpl;
import it.unibo.pcd.assignment.event.report.MethodInfoImpl;

import java.util.ArrayList;
import java.util.List;

public class ClassCollector extends VoidVisitorAdapter<ClassReportImpl> {
    @Override
    public void visit(ClassOrInterfaceDeclaration dec, ClassReportImpl collector) {
        super.visit(dec, collector);

        if (dec.isInnerClass()) {
            ClassReportImpl myInnerReport = this.createClassReport(new ClassReportImpl(), dec);
            if (collector.getInnerClass() == null) {
                collector.setInnerClass(myInnerReport);
            } else {
                ClassReportImpl innerClassino = collector.getInnerClass();
                this.addInnerClassReportToTail(myInnerReport, innerClassino);
                collector.setInnerClass(innerClassino);
            }
        } else {
            this.createClassReport(collector, dec);
        }
    }

    private void addInnerClassReportToTail(ClassReportImpl classReportToAdd, ClassReportImpl classReport) {
        if (classReport.getInnerClass() == null) {
            classReport.setInnerClass(classReportToAdd);
        } else {
            addInnerClassReportToTail(classReportToAdd, classReport.getInnerClass());
        }
    }

    private ClassReportImpl createClassReport(ClassReportImpl classReportToFill, ClassOrInterfaceDeclaration classDec) {
        classReportToFill.setFullClassName(classDec.getNameAsString());
        classReportToFill.setSrcFullFileName(classDec.getFullyQualifiedName().orElse("NULL!"));

        List<MethodInfoImpl> methodInfoList = this.createMethodInfoList(classDec);
        List<FieldInfoImpl> fieldInfoList = this.createFieldInfoList(classDec);

        methodInfoList.forEach(m -> m.setParentClass(classReportToFill));
        fieldInfoList.forEach(f -> f.setParentClass(classReportToFill));
        classReportToFill.setMethodsInfo(methodInfoList);
        classReportToFill.setFieldsInfo(fieldInfoList);
        return classReportToFill;
    }

    private List<MethodInfoImpl> createMethodInfoList(ClassOrInterfaceDeclaration dec) {
        List<MethodInfoImpl> methodInfoList = new ArrayList<>();

        dec.getMethods().forEach(m -> {
            MethodInfoImpl methodInfo = new MethodInfoImpl();
            methodInfo.setMain(m.isStatic() && m.getNameAsString().equals("main")
                    && m.getParameterByType(String[].class).isPresent()
                    && m.getParameters().size() == 1);
            methodInfo.setModifiers(m.getModifiers().toString());
            methodInfo.setBeginLine(m.getBegin().orElse(new Position(-1, -1)).line);
            methodInfo.setEndBeginLine(m.getEnd().orElse(new Position(-1, -1)).line);
            methodInfo.setName(m.getNameAsString());
            methodInfoList.add(methodInfo);
        });

        return methodInfoList;
    }

    private List<FieldInfoImpl> createFieldInfoList(ClassOrInterfaceDeclaration dec) {
        List<FieldInfoImpl> fieldInfoList = new ArrayList<>();

        dec.getFields().forEach(f -> {
            FieldInfoImpl fieldInfo = new FieldInfoImpl();
            fieldInfo.setName(f.getVariable(0).getName().asString());
            fieldInfo.setType(f.getElementType().asString());
            fieldInfoList.add(fieldInfo);
        });

        return fieldInfoList;
    }
}
