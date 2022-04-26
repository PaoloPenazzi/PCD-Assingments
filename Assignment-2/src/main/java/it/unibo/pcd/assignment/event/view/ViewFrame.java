package it.unibo.pcd.assignment.event.view;

import javax.swing.*;
import java.awt.*;

public class ViewFrame extends JFrame {
    private JButton openProjectButton;
    private JButton startAnalysisButton;
    private JButton stopAnalysisButton;
    private JTextArea consoleTextArea;
    private JTextField textPackage;
    private JTextField textClass;
    private JTextField textInterface;
    private JLabel fileSelectedLabel;
    private JPanel northPanel;
    private JScrollPane centralPanel;
    private JPanel bottomPanel;
    private final ViewController controller;

    public ViewFrame(ViewController controller) {
        this.controller = controller;
        this.setSize(620, 620);
        this.setTitle("Project Analyzer");
        this.setLayout(new BorderLayout());
        this.createNorthPanel();
        this.createCentralPanel();
        this.createBottomPanel();
        this.getContentPane().add(BorderLayout.NORTH, this.northPanel);
        this.getContentPane().add(BorderLayout.CENTER, this.centralPanel);
        this.getContentPane().add(BorderLayout.SOUTH, this.bottomPanel);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setVisible(true);
    }
    private void createBottomPanel() {
        this.bottomPanel = new JPanel(new GridLayout(4,2));
        this.fileSelectedLabel = new JLabel("Ready");

        JLabel labelPackage = new JLabel("Package: ");
        this.textPackage = new JTextField();
        this.textPackage.setEditable(false);

        JLabel labelClass = new JLabel("Class: ");
        this.textClass = new JTextField();
        this.textClass.setEditable(false);

        JLabel labelInterface = new JLabel("Interface: ");
        this.textInterface = new JTextField();
        this.textInterface.setEditable(false);

        this.bottomPanel.add(labelPackage);
        this.bottomPanel.add(textPackage);
        this.bottomPanel.add(labelClass);
        this.bottomPanel.add(textClass);
        this.bottomPanel.add(labelInterface);
        this.bottomPanel.add(textInterface);
        this.bottomPanel.add(this.fileSelectedLabel);
    }

    private void createNorthPanel() {
        this.northPanel = new JPanel();
        this.openProjectButton = new JButton("Open Project");
        this.startAnalysisButton = new JButton("Start");
        this.stopAnalysisButton = new JButton("Stop");
        this.startAnalysisButton.addActionListener(controller::startAnalysisPressed);
        this.openProjectButton.addActionListener(controller::openProjectPressed);
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

    public JTextField getTextPackage() {
        return this.textPackage;
    }

    public JTextField getTextClass() {
        return this.textClass;
    }

    public JTextField getTextInterface() {
        return this.textInterface;
    }
}
