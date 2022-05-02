package it.unibo.pcd.assignment.reactive.view;

import io.reactivex.rxjava3.core.Scheduler;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.internal.schedulers.NewThreadScheduler;
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
            this.clearConsoleOutput();
            this.createObservers();
            this.reactiveAnalyzerImpl.analyzeProject(this.reactiveAnalyzerImpl.getPath());
        }
    }

    // TODO check if this method is working correctly.
    public void stopPressed(ActionEvent actionEvent) {
        this.packageObserver.dispose();
        this.classObserver.dispose();
        this.interfaceObserver.dispose();
    }

    private void createObservers() {
        this.setupInterfaceNumberObserver();
        this.setupClassNumberObserver();
        this.setupPackageNumberObserver();
    }

    private void clearConsoleOutput() {
        this.view.getConsoleTextArea().selectAll();
        this.view.getConsoleTextArea().replaceSelection("");
    }

    private void setupPackageNumberObserver() {
        this.packageObserver = this.reactiveAnalyzerImpl.getPackageNumberObservable()
                .observeOn(Schedulers.newThread())
                .subscribeOn(Schedulers.newThread())
                .subscribe(num -> view.getPackageCounterTextField().setText("" + num));
    }

    private void setupClassNumberObserver(){
        this.classObserver = reactiveAnalyzerImpl.getClassNumberObservable()
                .subscribe(num -> view.getClassCounterTextField().setText("" + num));
    }

    private void setupInterfaceNumberObserver() {
        this.interfaceObserver = this.reactiveAnalyzerImpl.getInterfaceNumberObservable()
                .subscribe(num -> view.getInterfaceCounterTextField().setText("" + num));
    }
}
