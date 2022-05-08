package it.unibo.pcd.assignment.event.collector;

import com.github.javaparser.ParseResult;
import com.github.javaparser.ParserConfiguration;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.PackageDeclaration;
import com.github.javaparser.ast.body.BodyDeclaration;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.TypeDeclaration;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import com.github.javaparser.utils.SourceRoot;
import it.unibo.pcd.assignment.event.report.*;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class PackageCollector extends VoidVisitorAdapter<PackageReportImpl> {
    private String path;

    @Override
    public void visit(PackageDeclaration dec, PackageReportImpl collector) {
        super.visit(dec, collector);
        // name
        collector.setFullPackageName(dec.getNameAsString());

        // classes/interfaces report
        List<CompilationUnit> classesOrInterfacesUnit = this.createParsedFileList(dec).stream()
                .filter(ParseResult::isSuccessful)
                .filter(r -> r.getResult().isPresent())
                .map(r -> r.getResult().get())
                .collect(Collectors.toList());

        ClassCollector classCollector = new ClassCollector();
        List<ClassReport> classReports = new ArrayList<>();

        InterfaceCollector interfaceCollector = new InterfaceCollector();
        List<InterfaceReport> interfaceReports = new ArrayList<>();

        for (CompilationUnit cu : classesOrInterfacesUnit) {
            List<ClassOrInterfaceDeclaration> declarationList = cu.getTypes().stream()
                    .map(TypeDeclaration::asTypeDeclaration)
                    .filter(BodyDeclaration::isClassOrInterfaceDeclaration)
                    .map(x -> (ClassOrInterfaceDeclaration) x)
                    .collect(Collectors.toList());

            for (ClassOrInterfaceDeclaration declaration : declarationList) {
                ClassReportImpl classReport = new ClassReportImpl();
                InterfaceReportImpl interfaceReport = new InterfaceReportImpl();
                if (this.isTheRightPackage(dec.getNameAsString(), declaration)) {
                    if (declaration.isInterface()) {
                        interfaceCollector.visit(cu, interfaceReport);
                        interfaceReports.add(interfaceReport);
                    } else {
                        classCollector.visit(cu, classReport);
                        classReports.add(classReport);
                    }
                }
            }
        }

        collector.setClassReports(classReports);
        collector.setInterfaceReports(interfaceReports);
    }

    private boolean isTheRightPackage(String packageName, ClassOrInterfaceDeclaration declaration) {
        String classFullName = declaration.getFullyQualifiedName().get();
        String className = declaration.getNameAsString();
        classFullName = classFullName.replace("." + className, "");
        return classFullName.equals(packageName);
    }

    public void setPath(String path) {
        this.path = path;
    }

    private List<ParseResult<CompilationUnit>> createParsedFileList(PackageDeclaration dec) {
        if (!this.path.equals("")) {
            SourceRoot sourceRoot = new SourceRoot(Paths.get(this.path)).setParserConfiguration(new ParserConfiguration());
            List<ParseResult<CompilationUnit>> parseResultList;
            parseResultList = sourceRoot.tryToParseParallelized(dec.getNameAsString());
            return parseResultList;
        } else {
            SourceRoot sourceRoot = new SourceRoot(Paths.get("src/main/java/")).setParserConfiguration(new ParserConfiguration());
            List<ParseResult<CompilationUnit>> parseResultList;
            try {
                parseResultList = sourceRoot.tryToParse(dec.getNameAsString());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            return parseResultList;
        }
    }
}
