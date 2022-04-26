package it.unibo.pcd.assignment.event.view;

import javax.swing.*;
import java.awt.*;

public class ViewFrame extends JFrame {
    private JButton openProjectButton;
    private JButton startAnalysisButton;
    private JButton stopAnalysisButton;
    private JTextArea consoleTextArea;
    private JLabel fileSelectedLabel;
    private JPanel northPanel;
    private JScrollPane centralPanel;
    private JPanel bottomPanel;
    private final ViewController controller;

    public ViewFrame(ViewController controller) {
        this.controller = controller;
        this.setSize(620, 620);
        this.setTitle("Project Analyzer");
        this.setResizable(false);
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
        this.bottomPanel = new JPanel();
        this.fileSelectedLabel = new JLabel("Ready");
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
        this.consoleTextArea.setSize(620, 400);
        this.centralPanel = new JScrollPane(this.consoleTextArea);
    }

    public JLabel getFileSelectedLabel() {
        return fileSelectedLabel;
    }

    public JTextArea getConsoleTextArea() {
        return consoleTextArea;
    }
}
