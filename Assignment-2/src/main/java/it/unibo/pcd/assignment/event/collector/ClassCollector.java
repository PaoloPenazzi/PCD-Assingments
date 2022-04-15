package it.unibo.pcd.assignment.event.collector;

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
        // name
        collector.setFullClassName(dec.getNameAsString());
        // src path
        collector.setSrcFullFileName(dec.getFullyQualifiedName().get());
        // info on methods
        List<MethodInfoImpl> methodInfoList = new ArrayList<>();
        dec.getMethods().forEach(m -> {
            MethodInfoImpl methodInfo = new MethodInfoImpl();
            methodInfo.setMain(m.isStatic() && m.getNameAsString().equals("main")
                                && m.getParameterByType(String[].class).isPresent()
                                && m.getParameters().size() == 1 );
            methodInfo.setModifiers(m.getModifiers().toString());
            methodInfo.setBeginLine(m.getBegin().get().line);
            methodInfo.setEndBeginLine(m.getEnd().get().line);
            methodInfo.setName(m.getNameAsString());
            methodInfoList.add(methodInfo);
        });
        // info on fields
        List<FieldInfoImpl> fieldInfoList = new ArrayList<>();
        dec.getFields().forEach(f -> {
            FieldInfoImpl fieldInfo = new FieldInfoImpl();
            fieldInfo.setName(f.getVariable(0).getName().asString());
            // getVariable() returns a list because it's possible to declare more fields in the same line.
            fieldInfo.setType(f.getElementType().asString());
            fieldInfoList.add(fieldInfo);
        });
        // settato qui perchè si deve richiamare solo quando ha completato il class report
        methodInfoList.forEach(m -> m.setParentClass(collector));
        fieldInfoList.forEach(f -> f.setParentClass(collector));
        collector.setMethodsInfo(methodInfoList);
        collector.setFieldsInfo(fieldInfoList);
    }
}
