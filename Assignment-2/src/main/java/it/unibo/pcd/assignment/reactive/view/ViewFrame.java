package it.unibo.pcd.assignment.reactive.view;

import it.unibo.pcd.assignment.reactive.controller.ViewController;

import javax.swing.*;
import java.awt.*;

public class ViewFrame extends JFrame {
    private final ViewController controller;
    private JButton openProjectButton;
    private JButton startAnalysisButton;
    private JButton stopAnalysisButton;
    private JTextArea consoleTextArea;
    private JTextField packageCounterTextField;
    private JTextField classCounterTextField;
    private JTextField interfaceCounterTextField;
    private JLabel fileSelectedLabel;
    private JPanel northPanel;
    private JScrollPane centralPanel;
    private JPanel bottomPanel;

    public ViewFrame(ViewController controller) {
        this.controller = controller;
        this.setSize(1200, 620);
        this.setLocationRelativeTo(null);
        this.setTitle("Project Analyzer");
        this.setLayout(new BorderLayout());
        this.createAndAddPanels();
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setVisible(true);
    }

    private void createAndAddPanels() {
        this.createNorthPanel();
        this.createCentralPanel();
        this.createBottomPanel();
        this.getContentPane().add(BorderLayout.NORTH, this.northPanel);
        this.getContentPane().add(BorderLayout.CENTER, this.centralPanel);
        this.getContentPane().add(BorderLayout.SOUTH, this.bottomPanel);
    }

    private void createBottomPanel() {
        JPanel subPanelNorth = new JPanel(new GridLayout(3, 2));
        JPanel subPanelSouth = new JPanel();
        this.fileSelectedLabel = new JLabel("Ready");
        JLabel labelPackage = new JLabel("Package: ");
        this.packageCounterTextField = new JTextField();
        this.packageCounterTextField.setEditable(false);
        JLabel labelClass = new JLabel("Class: ");
        this.classCounterTextField = new JTextField();
        this.classCounterTextField.setEditable(false);
        JLabel labelInterface = new JLabel("Interface: ");
        this.interfaceCounterTextField = new JTextField();
        this.interfaceCounterTextField.setEditable(false);
        subPanelNorth.add(labelPackage);
        subPanelNorth.add(packageCounterTextField);
        subPanelNorth.add(labelClass);
        subPanelNorth.add(classCounterTextField);
        subPanelNorth.add(labelInterface);
        subPanelNorth.add(interfaceCounterTextField);
        subPanelSouth.add(this.fileSelectedLabel);
        this.bottomPanel = new JPanel(new BorderLayout());
        this.bottomPanel.add(BorderLayout.NORTH, subPanelNorth);
        this.bottomPanel.add(BorderLayout.SOUTH, subPanelSouth);
    }

    private void createNorthPanel() {
        this.northPanel = new JPanel();
        this.openProjectButton = new JButton("Open Project");
        this.startAnalysisButton = new JButton("Start");
        this.stopAnalysisButton = new JButton("Stop");
        this.startAnalysisButton.addActionListener(controller::startPressed);
        this.openProjectButton.addActionListener(controller::openProjectPressed);
        this.stopAnalysisButton.addActionListener(controller::stopPressed);
        this.northPanel.add(this.openProjectButton);
        this.northPanel.add(this.startAnalysisButton);
        this.northPanel.add(this.stopAnalysisButton);
    }

    private void createCentralPanel() {
        this.consoleTextArea = new JTextArea("");
        this.consoleTextArea.setLineWrap(true);
        this.consoleTextArea.setWrapStyleWord(true);
        this.consoleTextArea.setEditable(false);
        this.consoleTextArea.setSize(620, 300);
        this.centralPanel = new JScrollPane(this.consoleTextArea);
    }

    public JLabel getFileSelectedLabel() {
        return fileSelectedLabel;
    }

    public JTextArea getConsoleTextArea() {
        return consoleTextArea;
    }

    public JTextField getPackageCounterTextField() {
        return this.packageCounterTextField;
    }

    public JTextField getClassCounterTextField() {
        return this.classCounterTextField;
    }

    public JTextField getInterfaceCounterTextField() {
        return this.interfaceCounterTextField;
    }
}
