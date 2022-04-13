package it.unibo.pcd.assignment.event;

import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import java.util.List;

/*
It is often useful during traversal to be able to maintain a record of what we have seen so far. This
may be helpful for our current decision-making process, or we may want to collect all occurrences
of something deemed interesting.
The second parameter to the visit method is there to offer a simple state mechanism that is passed
around during the traversal of the tree. This gives us the option of having the visitor perform an
action, the visitor’s responsibility will be to collect the items i.e. method names. Then what the
invoking class does with the method names collected is then up to them.
 */
class MethodNameCollector extends VoidVisitorAdapter<List<String>> {
    public void visit(MethodDeclaration md, List<String> collector) {
        super.visit(md, collector);
        collector.add(md.getNameAsString());
    }
}
