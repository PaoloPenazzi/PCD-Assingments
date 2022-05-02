package it.unibo.pcd.assignment.reactive.view;

import io.reactivex.rxjava3.disposables.Disposable;
import it.unibo.pcd.assignment.reactive.model.ReactiveAnalyzer;
import javax.swing.*;
import java.awt.event.ActionEvent;

public class ViewController {
    private final ViewFrame view;
    private ReactiveAnalyzer reactiveAnalyzer;
    private Disposable packageObserver;
    private Disposable classObserver;
    private Disposable interfaceObserver;

    public ViewController() {
        this.view = new ViewFrame(this);
        this.reactiveAnalyzer = new ReactiveAnalyzer();
        this.setupPackageNumberObserver();
        this.setupClassNumberObserver();
        this.setupInterfaceNumberObserver();
    }

    public void openProjectPressed(ActionEvent actionEvent) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
        fileChooser.showSaveDialog(fileChooser);
        String path = fileChooser.getSelectedFile().getPath();
        this.reactiveAnalyzer.setPath(path);
        view.getFileSelectedLabel().setText(path);
    }

    public void startPressed(ActionEvent actionEvent) {
        if (!this.reactiveAnalyzer.getPath().equals("")) {
            this.clearConsoleOutput();
            this.createObservers();
            this.reactiveAnalyzer.analyzeProject();
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
        this.packageObserver = this.reactiveAnalyzer.getPackageNumberObservable()
                .subscribe(num -> view.getPackageCounterTextField().setText("" + num));
    }

    private void setupClassNumberObserver(){
        this.classObserver = reactiveAnalyzer.getClassNumberObservable()
                .subscribe(num -> view.getClassCounterTextField().setText("" + num));
    }

    private void setupInterfaceNumberObserver() {
        this.interfaceObserver = this.reactiveAnalyzer.getInterfaceNumberObservable()
                .subscribe(num -> view.getInterfaceCounterTextField().setText("" + num));
    }
}
