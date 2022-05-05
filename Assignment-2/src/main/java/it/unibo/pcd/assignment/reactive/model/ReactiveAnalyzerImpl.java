package it.unibo.pcd.assignment.reactive.model;

import com.github.javaparser.ParseResult;
import com.github.javaparser.ParserConfiguration;
import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.PackageDeclaration;
import com.github.javaparser.ast.body.*;
import com.github.javaparser.utils.SourceRoot;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.subjects.PublishSubject;
import io.reactivex.rxjava3.subjects.Subject;

import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

public class ReactiveAnalyzerImpl implements ReactiveAnalyzer {
    private final Subject<Integer> packageNumberObservable = PublishSubject.create();
    private final Subject<Integer> classNumberObservable = PublishSubject.create();
    private final Subject<Integer> interfaceNumberObservable = PublishSubject.create();
    private final Subject<String> reportObservable = PublishSubject.create();
    private int packageNumber;
    private int classNumber;
    private int interfaceNumber;
    private String path;

    public ReactiveAnalyzerImpl() {
        this.path = "";
    }

    @Override
    public void analyzeProject(String srcProjectFolderName) {
        SourceRoot sourceRoot = new SourceRoot(Paths.get(srcProjectFolderName)).setParserConfiguration(new ParserConfiguration());
        List<ParseResult<CompilationUnit>> parseResultList = sourceRoot.tryToParseParallelized();
        List<PackageDeclaration> allCus = parseResultList.stream()
                .filter(r -> r.getResult().isPresent() && r.isSuccessful())
                .map(r -> r.getResult().get())
                .filter(c -> c.getPackageDeclaration().isPresent())
                .map(c -> c.getPackageDeclaration().get())
                .distinct()
                .collect(Collectors.toList());
        for (PackageDeclaration packageDeclaration : allCus) {
            this.analyzePackage(packageDeclaration.getNameAsString());
        }
    }

    @Override
    public void analyzePackage(String packagePath) {
        this.addReport("PACKAGE:  " + packagePath + "\n");
        incrementPackageNumber();
        PackageDeclaration packageDeclaration = StaticJavaParser
                .parsePackageDeclaration("package " + packagePath + ";");
        List<CompilationUnit> classesOrInterfacesUnit = this.createParsedFileList(packageDeclaration).stream()
                .filter(r -> r.isSuccessful() && r.getResult().isPresent())
                .map(r -> r.getResult().get())
                .collect(Collectors.toList());
        for (CompilationUnit cu : classesOrInterfacesUnit) {
            // prendiamo tutte le dichiarazione delle classi/interface
            List<ClassOrInterfaceDeclaration> declarationList = cu.getTypes().stream()
                    .map(TypeDeclaration::asTypeDeclaration)
                    .filter(BodyDeclaration::isClassOrInterfaceDeclaration)
                    .map(ClassOrInterfaceDeclaration.class::cast)
                    .collect(Collectors.toList());
            for (ClassOrInterfaceDeclaration declaration : declarationList) {
                if (declaration.isInterface()) {
                    this.analyzeInterface(packagePath, declaration);
                } else {
                    this.analyzeClass(packagePath, declaration);
                }
            }
        }
    }

    private List<ParseResult<CompilationUnit>> createParsedFileList(PackageDeclaration packageDeclaration) {
        SourceRoot sourceRoot = new SourceRoot(Paths.get(this.path)).setParserConfiguration(new ParserConfiguration());
        List<ParseResult<CompilationUnit>> parseResultList;
        parseResultList = sourceRoot.tryToParseParallelized(packageDeclaration.getNameAsString());
        return parseResultList;
    }

    private void analyzeClass(String packageName, ClassOrInterfaceDeclaration declaration) {
        String className = declaration.getFullyQualifiedName().get();
        if (this.isRightPackage(packageName, declaration)) {
            this.buildClassReport(className, declaration);
            incrementClassNumber();
        }
    }

    private void analyzeInterface(String packageName, ClassOrInterfaceDeclaration declaration) {
        String interfaceName = declaration.getFullyQualifiedName().get();
        if (this.isRightPackage(packageName, declaration)) {
            incrementInterfaceNumber();
            buildInterfaceReport(interfaceName, declaration);

        }
    }

    private boolean isRightPackage(String packageName, ClassOrInterfaceDeclaration declaration) {
        String classFullName = declaration.getFullyQualifiedName().get();
        String className = declaration.getNameAsString();
        classFullName = classFullName.replace("." + className, "");
        return classFullName.equals(packageName);
    }

    private void buildClassReport(String className, ClassOrInterfaceDeclaration declaration) {
        String fieldsString = "";
        String methodsString = "";
        List<FieldDeclaration> fieldDeclarationList = declaration.getFields();
        for (FieldDeclaration fieldDeclaration : fieldDeclarationList) {
            fieldsString = fieldsString.concat(
                    "\t\t" + "FIELD:  " +
                            fieldDeclaration.toString()
                            + "\n");
        }
        List<MethodDeclaration> methodDeclarationList = declaration.getMethods();
        for (MethodDeclaration methodDeclaration : methodDeclarationList) {
            methodsString = methodsString.concat(
                    "\t\t" + "METHOD:  " +
                            methodDeclaration.getDeclarationAsString(true, false, true)
                            + "\n");
        }
        String report =
                "\t" + "CLASS:  " + className + "\n"
                        + fieldsString
                        + methodsString;
        addReport(report);
    }

    private void buildInterfaceReport(String interfaceName, ClassOrInterfaceDeclaration declaration) {
        String methodsString = "";
        List<MethodDeclaration> methodDeclarationList = declaration.getMethods();
        for (MethodDeclaration methodDeclaration : methodDeclarationList) {
            methodsString = methodsString.concat(
                    "\t\t" + "METHOD:  " +
                            methodDeclaration.getDeclarationAsString(true, false, true)
                            + "\n");
        }
        String report =
                "\t" + "INTERFACE:  " + interfaceName + "\n"
                        + methodsString;
        addReport(report);
    }

    public void incrementPackageNumber() {
        this.packageNumberObservable.onNext(++this.packageNumber);
    }

    public void incrementClassNumber() {
        this.classNumberObservable.onNext(++this.classNumber);
    }

    public void incrementInterfaceNumber() {
        this.interfaceNumberObservable.onNext(++this.interfaceNumber);
    }

    public void addReport(String report) {
        reportObservable.onNext(report);
    }

    public Observable<Integer> getPackageNumberObservable() {
        return packageNumberObservable;
    }

    public Observable<Integer> getClassNumberObservable() {
        return classNumberObservable;
    }

    public Observable<Integer> getInterfaceNumberObservable() {
        return interfaceNumberObservable;
    }

    public Observable<String> getReportObservable() {
        return reportObservable;
    }

    public String getPath() {
        return this.path;
    }

    public void setPath(String newPath) {
        this.path = newPath;
    }

    public void resetCounters() {
        this.packageNumber = 0;
        this.classNumber = 0;
        this.interfaceNumber = 0;
        this.packageNumberObservable.onNext(this.packageNumber);
        this.classNumberObservable.onNext(this.classNumber);
        this.interfaceNumberObservable.onNext(this.interfaceNumber);
    }
}
