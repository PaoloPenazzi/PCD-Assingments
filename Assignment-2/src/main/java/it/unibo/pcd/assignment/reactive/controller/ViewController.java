package it.unibo.pcd.assignment.reactive.controller;

import io.reactivex.rxjava3.core.Scheduler;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import it.unibo.pcd.assignment.reactive.model.ReactiveAnalyzerImpl;
import it.unibo.pcd.assignment.reactive.view.ViewFrame;

import javax.swing.*;
import java.awt.event.ActionEvent;

public class ViewController {
    private final ViewFrame view;
    private final ReactiveAnalyzerImpl reactiveAnalyzerImpl;
    private boolean isStopped;
    Scheduler scheduler;
    Scheduler.Worker worker;
    private String analysisType;

    public ViewController() {
        this.view = new ViewFrame(this);
        this.reactiveAnalyzerImpl = new ReactiveAnalyzerImpl();
        this.scheduler = Schedulers.computation();
        this.createObservers();
    }

    public void analyzePressed(ActionEvent actionEvent) {
        JFileChooser fileChooser = new JFileChooser();
        JButton source = (JButton) actionEvent.getSource();
        switch (source.getText()) {
            case "Analyze Class" -> {
                fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
                this.analysisType = "class";
            }
            case "Analyze Interface" -> {
                fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
                this.analysisType = "interface";
            }
            case "Analyze Package" -> {
                fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                this.analysisType = "package";
            }
            case "Analyze Project" -> {
                fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                this.analysisType = "project";
            }
            default -> throw new IllegalStateException("Unexpected behaviour");
        }
        fileChooser.showSaveDialog(fileChooser);
        if (fileChooser.getSelectedFile() != null) {
            String path = fileChooser.getSelectedFile().getPath();
            this.reactiveAnalyzerImpl.setPath(path);
            view.getFileSelectedLabel().setText(path);
        }
    }

    public void startPressed(ActionEvent actionEvent) {
        if (!this.reactiveAnalyzerImpl.getPath().equals("")) {
            this.isStopped = false;
            this.clearOutput();
            this.worker = this.scheduler.createWorker();
            switch (this.analysisType) {
                case "class" -> {
                    // TODO
                }
                case "interface" -> {
                    // TODO
                }
                case "package" -> {
                    this.worker.schedule(() -> {
                        this.reactiveAnalyzerImpl.analyzePackage(this.reactiveAnalyzerImpl.getPath());
                    });
                }
                case "project" -> {
                    this.worker.schedule(() -> {
                        this.reactiveAnalyzerImpl.analyzeProject(this.reactiveAnalyzerImpl.getPath());
                    });
                }
                default -> throw new IllegalStateException("Unexpected behaviour");
            }
        } else {
            JDialog dialog = new JDialog();
            dialog.add(new JLabel("Please select a file or a directory"));
            dialog.setSize(250, 100);
            dialog.setLocationRelativeTo(null);
            dialog.setVisible(true);
        }
    }

    public void stopPressed(ActionEvent actionEvent) {
        this.isStopped = true;
        this.worker.dispose();
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
        Disposable reportObserver = this.reactiveAnalyzerImpl.getReportObservable()
                .subscribeOn(Schedulers.computation())
                .subscribe(res -> {
                    if (!this.isStopped) {
                        view.getConsoleTextArea().append(res + "");
                    }
                });
    }

    private void setupPackageNumberObserver() {
        Disposable packageObserver = this.reactiveAnalyzerImpl.getPackageNumberObservable()
                .subscribeOn(Schedulers.computation())
                .subscribe(res -> {
                    if (!this.isStopped) {
                        view.getPackageCounterTextField().setText(res + "");
                    }
                });
    }

    private void setupClassNumberObserver() {
        Disposable classObserver = reactiveAnalyzerImpl.getClassNumberObservable()
                .subscribeOn(Schedulers.computation())
                .subscribe(res -> {
                    if (!this.isStopped) {
                        view.getClassCounterTextField().setText(res + "");
                    }
                });
    }

    private void setupInterfaceNumberObserver() {
        Disposable interfaceObserver = this.reactiveAnalyzerImpl.getInterfaceNumberObservable()
                .subscribeOn(Schedulers.computation())
                .subscribe(res -> {
                    if (!this.isStopped) {
                        view.getInterfaceCounterTextField().setText(res + "");
                    }
                });
    }
}
