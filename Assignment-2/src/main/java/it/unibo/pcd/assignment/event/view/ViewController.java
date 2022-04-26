package it.unibo.pcd.assignment.event.view;

import it.unibo.pcd.assignment.event.ProjectAnalyzerImpl;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.util.Objects;

public class ViewController {
    private final JTextArea outputConsole;
    private final ViewFrame view;
    private final ProjectAnalyzerImpl projectAnalyzer;

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
            this.projectAnalyzer.analyzeProject(ProjectAnalyzerImpl.PATH, System.out::println);
        }
    }

    public void log(String message) {
        this.outputConsole.append(message + "\n");
    }
}
