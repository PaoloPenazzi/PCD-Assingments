package it.unibo.pcd.assignment.event;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import java.io.File;

public class TestJavaParser {

	public static void main(String[] args) throws Exception {
		CompilationUnit cu = StaticJavaParser.parse(new File("src/pcd/ass02/TestJavaParser.java"));
		/*
		var methodNames = new ArrayList<String>();
		var methodNameCollector = new MethodNameCollector();
		methodNameCollector.visit(cu,methodNames);
		methodNames.forEach(n -> System.out.println("MethodNameCollected:" + n));
		 */
		var fullc = new FullCollector();
		fullc.visit(cu, null);
		
	}
}
