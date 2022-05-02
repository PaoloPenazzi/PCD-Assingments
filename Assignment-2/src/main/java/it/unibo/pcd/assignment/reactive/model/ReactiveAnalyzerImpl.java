package it.unibo.pcd.assignment.reactive.model;

import com.github.javaparser.ParseResult;
import com.github.javaparser.ParserConfiguration;
import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.PackageDeclaration;
import com.github.javaparser.ast.body.BodyDeclaration;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.TypeDeclaration;
import com.github.javaparser.utils.SourceRoot;
import hu.webarticum.treeprinter.SimpleTreeNode;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.subjects.PublishSubject;
import io.reactivex.rxjava3.subjects.Subject;
import io.vertx.core.Future;
import it.unibo.pcd.assignment.event.ProjectAnalyzerImpl;
import it.unibo.pcd.assignment.event.report.ClassReport;
import it.unibo.pcd.assignment.event.report.InterfaceReport;
import it.unibo.pcd.assignment.event.report.PackageReport;
import it.unibo.pcd.assignment.event.report.ProjectReportImpl;

import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class ReactiveAnalyzerImpl implements ReactiveAnalyzer {
    private int packageNumber;
    private int classNumber;
    private int interfaceNumber;
    private final Subject<Integer> packageNumberObservable = PublishSubject.create();
    private final Subject<Integer> classNumberObservable = PublishSubject.create();
    private final Subject<Integer> interfaceNumberObservable = PublishSubject.create();
    private final Subject<String> reportObservable = PublishSubject.create();
    private String path;
    private final List<String> filesAlreadyAnalyzed;

    public ReactiveAnalyzerImpl() {
        this.path = "";
        this.filesAlreadyAnalyzed = new ArrayList<>();
    }

    @Override
    public void analyzeProject(String srcProjectFolderName) {
        SourceRoot sourceRoot = new SourceRoot(Paths.get(srcProjectFolderName)).setParserConfiguration(new ParserConfiguration());
        List<ParseResult<CompilationUnit>> parseResultList  = sourceRoot.tryToParseParallelized();
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

    private void analyzePackage(String packageName) {
        if(!filesAlreadyAnalyzed.contains(packageName)) {
            this.filesAlreadyAnalyzed.add(packageName);
            incrementPackageNumber();
            setReport(packageName);
            PackageDeclaration packageDeclaration = StaticJavaParser
                    .parsePackageDeclaration("package " + packageName + ";");
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
                        this.analyzeInterface(declaration.getFullyQualifiedName().get());
                    } else {
                        this.analyzeClass(declaration.getFullyQualifiedName().get());
                    }
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

    private void analyzeClass(String className) {
        if(!this.filesAlreadyAnalyzed.contains(className)) {
            this.filesAlreadyAnalyzed.add(className);
            incrementClassNumber();
            setReport(className);
        }
    }

    private void analyzeInterface(String interfaceName) {
        if(!this.filesAlreadyAnalyzed.contains(interfaceName)) {
            System.out.println(this.filesAlreadyAnalyzed);
            this.filesAlreadyAnalyzed.add(interfaceName);
            incrementInterfaceNumber();
            setReport(interfaceName);
        }
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

    public void setReport(String report) {
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
        this.packageNumberObservable.onNext(this.packageNumber);
        this.classNumber = 0;
        this.classNumberObservable.onNext(this.classNumber);
        this.interfaceNumber = 0;
        this.interfaceNumberObservable.onNext(this.interfaceNumber);
        this.setReport("");
    }
}
