package it.unibo.pcd.assignment.reactive.controller;

import io.reactivex.rxjava3.core.Scheduler;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import it.unibo.pcd.assignment.parser.ProjectElem;
import it.unibo.pcd.assignment.reactive.ReactiveAnalyzerImpl;
import it.unibo.pcd.assignment.reactive.view.ViewFrame;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.event.ActionEvent;

public class ViewController {
    private final ViewFrame view;
    private final ReactiveAnalyzerImpl reactiveAnalyzerImpl;
    Scheduler scheduler;
    Scheduler.Worker worker;
    private Disposable observer;

    public ViewController() {
        this.view = new ViewFrame(this);
        this.reactiveAnalyzerImpl = new ReactiveAnalyzerImpl();
        this.scheduler = Schedulers.computation();
    }

    public void analyzePressed(ActionEvent actionEvent) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setAcceptAllFileFilterUsed(false);
        JButton source = (JButton) actionEvent.getSource();
        switch (source.getText()) {
            case "Class Report" -> {
                this.reactiveAnalyzerImpl.setAnalysisType("class");
                FileFilter filter = new FileNameExtensionFilter("", "java");
                fileChooser.setFileFilter(filter);
            }
            case "Interface Report" -> {
                this.reactiveAnalyzerImpl.setAnalysisType("interface");
                FileFilter filter = new FileNameExtensionFilter("", "java");
                fileChooser.setFileFilter(filter);
            }
            case "Package Report" -> {
                this.reactiveAnalyzerImpl.setAnalysisType("package");
                fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            }
            case "Project Report" -> {
                this.reactiveAnalyzerImpl.setAnalysisType("project");
                fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            }
            case "Analyze Project" -> {
                this.reactiveAnalyzerImpl.setAnalysisType("analysis");
                fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            }
        }
        fileChooser.showSaveDialog(fileChooser);
        if (fileChooser.getSelectedFile() != null) {
            String path = fileChooser.getSelectedFile().getPath();
            this.reactiveAnalyzerImpl.setAnalysisPath(path);
            view.getFileSelectedLabel().setText(path);
        }
    }

    public void startPressed() {
        if (!this.reactiveAnalyzerImpl.getAnalysisPath().equals("")) {
            this.clearOutput();
            this.worker = this.scheduler.createWorker();
            switch (this.reactiveAnalyzerImpl.getAnalysisType()) {
                case "class" ->
                        observer = this.reactiveAnalyzerImpl.getClassReport(this.reactiveAnalyzerImpl.getAnalysisPath())
                                .subscribe(this::log);
                case "interface" ->
                        observer = this.reactiveAnalyzerImpl.getInterfaceReport(this.reactiveAnalyzerImpl.getAnalysisPath())
                                .subscribe(this::log);
                case "package" ->
                        observer = this.reactiveAnalyzerImpl.getPackageReport(this.reactiveAnalyzerImpl.getAnalysisPath())
                                .subscribe(this::log);
                case "project" ->
                        observer = this.reactiveAnalyzerImpl.getProjectReport(this.reactiveAnalyzerImpl.getAnalysisPath())
                                .subscribe(this::log);
                case "analysis" ->
                        observer = this.reactiveAnalyzerImpl.analyzeProject(this.reactiveAnalyzerImpl.getAnalysisPath())
                                .subscribe(this::log);
            }
        } else {
            JDialog dialog = new JDialog();
            dialog.add(new JLabel("Please select a file or a directory"));
            dialog.setSize(250, 100);
            dialog.setLocationRelativeTo(null);
            dialog.setVisible(true);
        }
    }

    private void log(String report) {
        SwingUtilities.invokeLater(() -> {
            int indentation = 0;
            switch (report.substring(0, 3)) {
                case "Cla" -> {
                    this.increaseClassNumber();
                    indentation = 6;
                }
                case "Int" -> {
                    this.increaseInterfaceNumber();
                    indentation = 6;
                }
                case "Pac" -> this.increasePackageNumber();
            }
            view.getConsoleTextArea().append(report.indent(indentation) + "\n");
        });
    }

    private void log(ProjectElem report) {
        this.log(report.toString());
    }

    public void stopPressed() {
        this.observer.dispose();
    }

    private void clearOutput() {
        this.view.getPackageCounterTextField().setText("0");
        this.view.getClassCounterTextField().setText("0");
        this.view.getInterfaceCounterTextField().setText("0");
        this.view.getConsoleTextArea().selectAll();
        this.view.getConsoleTextArea().replaceSelection("");
    }

    private void increasePackageNumber() {
        int oldValue = Integer.parseInt(view.getPackageCounterTextField().getText());
        view.getPackageCounterTextField().setText(String.valueOf(++oldValue));
    }

    private void increaseClassNumber() {
        int oldValue = Integer.parseInt(view.getClassCounterTextField().getText());
        view.getClassCounterTextField().setText(String.valueOf(++oldValue));
    }

    private void increaseInterfaceNumber() {
        int oldValue = Integer.parseInt(view.getInterfaceCounterTextField().getText());
        view.getInterfaceCounterTextField().setText(String.valueOf(++oldValue));
    }
}
