package it.unibo.pcd.assignment.event;

import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;

public class MethodNamePrinter extends VoidVisitorAdapter<Void> {

    @Override
    public void visit(MethodDeclaration md, Void arg) {
        super.visit(md, arg);
        System.out.println("Method type printed: " + md.getType());
        System.out.println("Method type printed: " + md.getType());
        System.out.println("Method name printed: " + md.getName());
    }
}
