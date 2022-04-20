package it.unibo.pcd.assignment.event.collector;

import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import it.unibo.pcd.assignment.event.report.InterfaceReportImpl;

import java.util.ArrayList;
import java.util.List;

public class InterfaceCollector extends VoidVisitorAdapter<InterfaceReportImpl> {
    @Override
    public void visit(ClassOrInterfaceDeclaration dec, InterfaceReportImpl collector) {
        super.visit(dec, collector);
        collector.setInterfaceName(dec.getNameAsString());
        collector.setFullFileName(dec.getFullyQualifiedName().orElse("NULL!"));
        collector.setMethodNameList(this.createMethodNameList(dec));
    }

    private List<String> createMethodNameList(ClassOrInterfaceDeclaration dec){
        List<String> methodNameList = new ArrayList<>();
        dec.getMethods().forEach(m -> {
            methodNameList.add(m.getName().asString());
        });
        return methodNameList;
    }
}
