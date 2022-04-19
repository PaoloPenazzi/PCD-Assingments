package it.unibo.pcd.assignment.event;

public class TestJavaParser {

    private static final String FILE_PATH = "src/main/java/it/unibo/pcd/assignment/event/TestClassReversePolishNotation.java";

    public static void main(String[] args) throws Exception {
		/*CompilationUnit cu = StaticJavaParser.parse(new File(FILE_PATH));

		VoidVisitor<Void> methodNameVisitor = new MethodNamePrinter();
		methodNameVisitor.visit(cu, null);


		List<String> methodNames = new ArrayList<>();
		VoidVisitor<List<String>> methodNameCollector = new MethodNameCollector();
		methodNameCollector.visit(cu, methodNames);
		methodNames.forEach(n -> System.out.println("Method Name Collected: " + n));

		/*
		// JavaParser and StaticJavaParser provides an API for producing asn AST from code
		// CompilationUnit is the root of the AST
		// Visitors are classes which are used to find specific parts of the AST
		JavaParser javaParser = new JavaParser();
		The CompilationUnit is the Java representation of source code from a complete and syntactically
		correct class file you have parsed. In the context of an AST as mentioned, you can think of the
		class as the root node.
		From here you can access all the nodes of the tree to examine their properties, manipulate the
		underlying Java representation or use it as an entry point for a Visitor you have defined.
		CompilationUnit cu = StaticJavaParser.parse(new File("src/main/java/it/unibo/pcd/assignment/event/TestJavaParser.java"));
		/*
		var methodNames = new ArrayList<String>();
		var methodNameCollector = new MethodNameCollector();
		methodNameCollector.visit(cu,methodNames);
		methodNames.forEach(n -> System.out.println("MethodNameCollected:" + n));

		var fullc = new FullCollector();
		fullc.visit(cu, null);
		*/


        // ProjectAnalyzerImpl projectAnalyzer = new ProjectAnalyzerImpl();
        //projectAnalyzer.getClassReport(null);

    }

}
