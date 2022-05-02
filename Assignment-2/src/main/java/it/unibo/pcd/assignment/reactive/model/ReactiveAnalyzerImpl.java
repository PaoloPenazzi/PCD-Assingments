package it.unibo.pcd.assignment.reactive.model;

import com.github.javaparser.ParseResult;
import com.github.javaparser.ParserConfiguration;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.PackageDeclaration;
import com.github.javaparser.utils.SourceRoot;
import com.sun.jdi.ThreadReference;
import hu.webarticum.treeprinter.SimpleTreeNode;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.schedulers.Schedulers;
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
        this.report = "";
        this.filesAlreadyAnalyzed = new ArrayList<>();
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
            try {
                Thread.sleep(10);
                System.out.println(this.packageNumber);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            incrementPackageNumber();
            this.report = packageDeclaration.getNameAsString();
            this.newReport();
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

    public void newReport() {
        reportObservable.onNext(report);
    }

    public Observable<Integer> getPackageNumberObservable() {
        return packageNumberObservable;
    }

    public Subject<Integer> getClassNumberObservable() {
        return classNumberObservable;
    }

    public Subject<Integer> getInterfaceNumberObservable() {
        return interfaceNumberObservable;
    }

    public Subject<String> getReportObservable() {
        return reportObservable;
    }

    public String getPath() {
        return this.path;
    }

    public void setPath(String newPath) {
        this.path = newPath;
    }
}
