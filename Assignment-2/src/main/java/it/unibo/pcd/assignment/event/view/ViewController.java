package it.unibo.pcd.assignment.event.view;

import hu.webarticum.treeprinter.SimpleTreeNode;
import it.unibo.pcd.assignment.event.ProjectAnalyzerImpl;

import javax.swing.*;
import java.awt.event.ActionEvent;

public class ViewController {
    public static int CLASS_NUMBER = 0;
    public static int INTERFACE_NUMBER = 0;
    public static int PACKAGE_NUMBER = 0;
    private String analysisType;
    private final JTextArea outputConsole;
    private final ViewFrame view;
    private final ProjectAnalyzerImpl projectAnalyzer;

    public ViewController(ProjectAnalyzerImpl projectAnalyzer) {
        this.projectAnalyzer = projectAnalyzer;
        this.view = new ViewFrame(this);
        this.outputConsole = view.getConsoleTextArea();
    }

    public void analyzePressed(ActionEvent actionEvent) {
        JFileChooser fileChooser = new JFileChooser();
        JButton source = (JButton) actionEvent.getSource();
        switch (source.getText()) {
            case "Class Report" : {
                fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
                this.analysisType = "class";
                break;
            }
            case "Interface Report" : {
                fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
                this.analysisType = "interface";
                break;
            }
            case "Package Report" : {
                fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                this.analysisType = "package";
                break;
            }
            case "Project Report" : {
                fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                this.analysisType = "project";
                break;
            }
            case "Analyze Project" : {
                fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                this.analysisType = "analysis";
                break;
            }
            default : throw new IllegalStateException("Unexpected behaviour");

        }
        fileChooser.showSaveDialog(fileChooser);
        if (fileChooser.getSelectedFile() != null) {
            String path = fileChooser.getSelectedFile().getPath();
            this.projectAnalyzer.setPATH(path);
            view.getFileSelectedLabel().setText(path);
        }
    }

    public void startAnalyses(ActionEvent e){
        if (!this.projectAnalyzer.getPATH().equals("")) {
            this.view.getConsoleTextArea().selectAll();
            this.view.getConsoleTextArea().replaceSelection("");
            ViewController.CLASS_NUMBER = 0;
            ViewController.INTERFACE_NUMBER = 0;
            ViewController.PACKAGE_NUMBER = 0;
            this.view.getClassCounterTextField().setText("0");
            this.view.getInterfaceCounterTextField().setText("0");
            this.view.getPackageCounterTextField().setText("0");
            String path =this.projectAnalyzer.getPATH();
            switch (this.analysisType){
                case "class" : {
                    this.projectAnalyzer.getClassReport(path, new SimpleTreeNode("Class Analyses")).onComplete(res -> {
                        if(res.succeeded()){
                            this.log(res.result().toString());
                        }
                    });
                    break;
                }
                case "interface" : {
                    this.projectAnalyzer.getInterfaceReport(path, new SimpleTreeNode("Interface Analyses")).onComplete(res -> {
                        if(res.succeeded()){
                            this.log(res.result().toString());
                        }
                    });
                    break;
                }
                case "package" : {
                    this.projectAnalyzer.getPackageReport(path, new SimpleTreeNode("Package Analyses")).onComplete(res -> {
                        if(res.succeeded()){
                            this.log(res.result().toString());
                        }
                    });
                    break;
                }
                case "project" : {
                    this.projectAnalyzer.getProjectReport(path).onComplete(res -> {
                        if(res.succeeded()){
                            this.log(res.result().toString());
                        }
                    });
                    break;
                }
                case "analysis" : {
                    this.projectAnalyzer.analyzeProject(path, s -> this.log(s.toString()));
                    break;
                }
                default : throw new IllegalStateException("Unexpected behaviour");
            }
        } else {
            JDialog dialog = new JDialog();
            dialog.add(new JLabel("Please select a file or a directory"));
            dialog.setSize(250, 100);
            dialog.setLocationRelativeTo(null);
            dialog.setVisible(true);
        }
    }

    public void stopAnalyses(ActionEvent e) {
        try {
            this.projectAnalyzer.getVertx().close();
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    synchronized public void increasePackageNumber() {
        this.view.getPackageCounterTextField().setText(String.valueOf(++PACKAGE_NUMBER));
    }

    synchronized public void increaseClassNumber() {
        this.view.getClassCounterTextField().setText(String.valueOf(++CLASS_NUMBER));
    }

    synchronized public void increaseInterfaceNumber() {
        this.view.getInterfaceCounterTextField().setText(String.valueOf(++INTERFACE_NUMBER));
    }

    public void log(String message) {
        this.outputConsole.append(message + "\n");
    }

    public void clearScreen(){
        this.outputConsole.selectAll();
        this.outputConsole.replaceSelection("");
    }
}
