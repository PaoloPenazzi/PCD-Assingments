package it.unibo.pcd.assignment.reactive.model;

import com.github.javaparser.ParseResult;
import com.github.javaparser.ParserConfiguration;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.PackageDeclaration;
import com.github.javaparser.utils.SourceRoot;
import hu.webarticum.treeprinter.SimpleTreeNode;
import io.reactivex.rxjava3.subjects.PublishSubject;
import io.reactivex.rxjava3.subjects.Subject;
import it.unibo.pcd.assignment.event.report.PackageReport;
import it.unibo.pcd.assignment.event.report.ProjectReportImpl;

import java.nio.file.Paths;
import java.util.ArrayList;
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
    private String report;

    public ReactiveAnalyzerImpl() {
        this.packageNumber = 0;
        this.classNumber = 0;
        this.interfaceNumber = 0;
        this.path = "";
        this.filesAlreadyAnalyzed = new ArrayList<>();
        this.report = "";
    }

    @Override
    public void analyzeProject(String srcProjectFolderName) {
        ProjectReportImpl projectReport = new ProjectReportImpl();
        List<String> packageList = new ArrayList<>();
        List<PackageReport> packageReports = new ArrayList<>();

        SourceRoot sourceRoot = new SourceRoot(Paths.get(srcProjectFolderName)).setParserConfiguration(new ParserConfiguration());
        List<ParseResult<CompilationUnit>> parseResultList;
        parseResultList = sourceRoot.tryToParseParallelized();

        List<PackageDeclaration> allCus = parseResultList.stream()
                .filter(r -> r.getResult().isPresent() && r.isSuccessful())
                .map(r -> r.getResult().get())
                .filter(c -> c.getPackageDeclaration().isPresent())
                .map(c -> c.getPackageDeclaration().get())
                .distinct()
                .collect(Collectors.toList());

        SimpleTreeNode rootProject = new SimpleTreeNode("Root Project: " + srcProjectFolderName);

        for (PackageDeclaration packageDeclaration : allCus) {
            SimpleTreeNode packageNodeChild = new SimpleTreeNode("Package child: " + packageDeclaration.getNameAsString());
            rootProject.addChild(packageNodeChild);
            packageList.add(packageDeclaration.getNameAsString());
            incrementPackageNumber();
            this.report = packageDeclaration.getNameAsString();
        }
    }

    public void incrementPackageNumber() {
        packageNumberObservable.onNext(++this.packageNumber);
    }

    public void incrementClassNumber() {
        classNumberObservable.onNext(++this.classNumber);
    }

    public void incrementInterfaceNumber() {
        interfaceNumberObservable.onNext(++this.interfaceNumber);
    }

    public Subject<Integer> getPackageNumberObservable() {
        return packageNumberObservable;
    }

    public Subject<Integer> getClassNumberObservable() {
        return classNumberObservable;
    }

    public Subject<Integer> getInterfaceNumberObservable() {
        return interfaceNumberObservable;
    }

    public String getPath() {
        return this.path;
    }

    public void setPath(String newPath) {
        this.path = newPath;
    }
}
