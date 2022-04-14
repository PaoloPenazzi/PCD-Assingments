package it.unibo.pcd.assignment.event;

import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.expr.Name;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;

import java.util.ArrayList;
import java.util.List;

public class ClassCollector extends VoidVisitorAdapter<ClassReportImpl> {

    @Override
    public void visit(ClassOrInterfaceDeclaration dec, ClassReportImpl collector) {
        super.visit(dec, collector);
        collector.setFullClassName(dec.getNameAsString());
        List<MethodInfoImpl> methodInfoList = new ArrayList<>();
        dec.getMethods().forEach(m -> {
            MethodInfoImpl methodInfo = new MethodInfoImpl();
            methodInfo.setModifiers(m.getModifiers().toString());
            methodInfo.setBeginLine(m.getBegin().get().line);
            methodInfo.setEndBeginLine(m.getEnd().get().line);
            methodInfo.setName(m.getNameAsString());
            methodInfoList.add(methodInfo);
        });
        // settato qui perchè si deve richiamare solo quando ha completato il class report
        methodInfoList.forEach(m -> m.setParentClass(collector));



    }
}
