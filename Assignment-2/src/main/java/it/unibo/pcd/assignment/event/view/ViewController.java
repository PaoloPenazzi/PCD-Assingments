package it.unibo.pcd.assignment.event.view;

import it.unibo.pcd.assignment.event.ProjectAnalyzerImpl;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.util.Objects;

public class ViewController {
    private final JTextArea outputConsole;
    private final ViewFrame view;
    private final ProjectAnalyzerImpl projectAnalyzer;
    public static int CLASS_NUMBER = 0;
    public static int INTERFACE_NUMBER = 0;
    public static int PACKAGE_NUMBER = 0;

    public ViewController (ProjectAnalyzerImpl projectAnalyzer){
        this.projectAnalyzer = projectAnalyzer;
        this.view = new ViewFrame(this);
        this.outputConsole = view.getConsoleTextArea();
    }

    public void openProjectPressed(ActionEvent e) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
        fileChooser.showSaveDialog(fileChooser);
        String path = fileChooser.getSelectedFile().getPath();
        ProjectAnalyzerImpl.PATH = path;
        view.getFileSelectedLabel().setText(path);
    }

    public void startAnalysisPressed(ActionEvent e) {
        if (!Objects.equals(ProjectAnalyzerImpl.PATH, "")) {
            this.view.getConsoleTextArea().selectAll();
            this.view.getConsoleTextArea().replaceSelection("");
            ViewController.CLASS_NUMBER = 0;
            ViewController.INTERFACE_NUMBER = 0;
            ViewController.PACKAGE_NUMBER = 0;
            this.view.getTextClass().setText("0");
            this.view.getTextInterface().setText("0");
            this.view.getTextPackage().setText("0");
            this.projectAnalyzer.getAlreadyAnalyzed().clear();
            this.projectAnalyzer.analyzeProject(ProjectAnalyzerImpl.PATH, (k) -> this.log(k.toString()));
        }
    }

    public void stopAnalysisPressed(ActionEvent e) {
    }

    public void increasePackageNumber(){
        this.view.getTextPackage().setText(String.valueOf(++PACKAGE_NUMBER));
    }

    public void increaseClassNumber(){
        this.view.getTextClass().setText(String.valueOf(++CLASS_NUMBER));
    }

    public void increaseInterfaceNumber(){
        this.view.getTextInterface().setText(String.valueOf(++INTERFACE_NUMBER));
    }

    public void log(String message) {
        this.outputConsole.append(message + "\n");
    }
}
