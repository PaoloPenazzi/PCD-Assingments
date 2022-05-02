package it.unibo.pcd.assignment.reactive.view;

import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import it.unibo.pcd.assignment.reactive.model.ReactiveAnalyzerImpl;

import javax.swing.*;
import java.awt.event.ActionEvent;

public class ViewController {
    private final ViewFrame view;
    private ReactiveAnalyzerImpl reactiveAnalyzerImpl;
    private Disposable packageObserver;
    private Disposable classObserver;
    private Disposable interfaceObserver;
    private Disposable reportObserver;
    private Disposable runningProcess;
    private boolean isStopped;

    public ViewController() {
        this.view = new ViewFrame(this);
        this.reactiveAnalyzerImpl = new ReactiveAnalyzerImpl();
        this.setupPackageNumberObserver();
        this.setupClassNumberObserver();
        this.setupInterfaceNumberObserver();
    }

    public void openProjectPressed(ActionEvent actionEvent) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
        fileChooser.showSaveDialog(fileChooser);
        String path = fileChooser.getSelectedFile().getPath();
        this.reactiveAnalyzerImpl.setPath(path);
        view.getFileSelectedLabel().setText(path);
    }

    public void startPressed(ActionEvent actionEvent) {
        if (!this.reactiveAnalyzerImpl.getPath().equals("")) {
            this.clearOutput();
            this.createObservers();
            this.isStopped = false;
            this.runningProcess = Schedulers.computation().scheduleDirect( () ->
                    this.reactiveAnalyzerImpl.analyzeProject(this.reactiveAnalyzerImpl.getPath())
            );
        }
    }

    // TODO check if this method is working correctly.
    public void stopPressed(ActionEvent actionEvent) {
        this.runningProcess.dispose();
    }

    private void createObservers() {
        this.setupInterfaceNumberObserver();
        this.setupClassNumberObserver();
        this.setupPackageNumberObserver();
        this.setupReportObserver();
    }

    private void clearOutput() {
        this.reactiveAnalyzerImpl.resetCounters();
        this.view.getConsoleTextArea().selectAll();
        this.view.getConsoleTextArea().replaceSelection("");
    }

    private void setupReportObserver() {
        this.reportObserver = this.reactiveAnalyzerImpl.getReportObservable()
                .subscribeOn(Schedulers.computation())
                .subscribe(res -> view.getConsoleTextArea().append(res + "\n"));
    }

    private void setupPackageNumberObserver() {
        this.packageObserver = this.reactiveAnalyzerImpl.getPackageNumberObservable()
                .subscribeOn(Schedulers.computation())
                .subscribe(res -> {
                    if(!this.isStopped) {
                        view.getPackageCounterTextField().setText(res + "");
                    }
                });
    }

    private void setupClassNumberObserver() {
        this.classObserver = reactiveAnalyzerImpl.getClassNumberObservable()
                .subscribeOn(Schedulers.computation())
                .subscribe(num -> view.getClassCounterTextField().setText("" + num));
    }

    private void setupInterfaceNumberObserver() {
        this.interfaceObserver = this.reactiveAnalyzerImpl.getInterfaceNumberObservable()
                .subscribeOn(Schedulers.computation())
                .subscribe(num -> view.getInterfaceCounterTextField().setText("" + num));
    }
}
