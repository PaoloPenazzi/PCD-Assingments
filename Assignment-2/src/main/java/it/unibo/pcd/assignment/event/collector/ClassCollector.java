package it.unibo.pcd.assignment.event.collector;

import com.github.javaparser.Position;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import it.unibo.pcd.assignment.event.report.ClassReport;
import it.unibo.pcd.assignment.event.report.ClassReportImpl;
import it.unibo.pcd.assignment.event.report.FieldInfoImpl;
import it.unibo.pcd.assignment.event.report.MethodInfoImpl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ClassCollector extends VoidVisitorAdapter<ClassReportImpl> {
    @Override
    public void visit(ClassOrInterfaceDeclaration dec, ClassReportImpl collector) {
        // mi creo un nuovo report nel caso in cui ci siano classi innestate
        System.out.println("BBBBBBBBBBBBBB" + dec.getNameAsString());
        System.out.println(collector);
        ClassReportImpl innerClassReport = new ClassReportImpl();

        // faccio la visita di evenutali classi innestate e nel caso mi salvo le info sul nuovo class report creato
        super.visit(dec, innerClassReport);

        System.out.println(dec.getNameAsString());
        System.out.println(innerClassReport);

        if (innerClassReport.getFullClassName() != null) {
            collector.getInnerClassList().add(innerClassReport);
        }

        /*if (dec.isInnerClass()) {
            ClassReportImpl myInnerReport = new ClassReportImpl();

            myInnerReport.setFullClassName(dec.getNameAsString());
            myInnerReport.setSrcFullFileName(dec.getFullyQualifiedName().orElse("NULL!"));

            List<MethodInfoImpl> methodInfoList = this.createMethodInfoList(dec);
            List<FieldInfoImpl> fieldInfoList = this.createFieldInfoList(dec);

            methodInfoList.forEach(m -> m.setParentClass(collector));
            fieldInfoList.forEach(f -> f.setParentClass(collector));
            myInnerReport.setMethodsInfo(methodInfoList);
            myInnerReport.setFieldsInfo(fieldInfoList);

            collector.getInnerClassList().add(myInnerReport);

        } else {*/

            collector.setFullClassName(dec.getNameAsString());
            collector.setSrcFullFileName(dec.getFullyQualifiedName().orElse("NULL!"));

            List<MethodInfoImpl> methodInfoList = this.createMethodInfoList(dec);
            List<FieldInfoImpl> fieldInfoList = this.createFieldInfoList(dec);

            methodInfoList.forEach(m -> m.setParentClass(collector));
            fieldInfoList.forEach(f -> f.setParentClass(collector));
            collector.setMethodsInfo(methodInfoList);
            collector.setFieldsInfo(fieldInfoList);

            System.out.println(collector + "\n");
            
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
