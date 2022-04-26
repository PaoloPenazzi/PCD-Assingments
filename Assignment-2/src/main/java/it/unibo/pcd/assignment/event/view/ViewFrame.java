package it.unibo.pcd.assignment.event.view;

import javax.swing.*;
import java.awt.*;

public class ViewFrame extends JFrame {
    private JButton openProject;

    private JTextArea console;

    private JPanel controlPanel;
    private JScrollPane outputPanel;
    private ViewController controller;
    public ViewFrame(ViewController controller) {
        this.controller = controller;
        this.setSize(620, 620);
        this.setTitle("Project Analyzer");
        this.setLayout(new BorderLayout());
        this.createControlPanel();
        this.createConsole();
        this.getContentPane().add(BorderLayout.NORTH, this.controlPanel);
        this.getContentPane().add(BorderLayout.CENTER, this.outputPanel);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setVisible(true);
    }

    private void createControlPanel() {
        this.controlPanel = new JPanel();
        this.openProject = new JButton("Open Project");
        this.openProject.addActionListener(controller::actionPerformed);
        this.controlPanel.add(this.openProject);
    }

    private void createConsole() {
        this.console = new JTextArea("");
        this.console.setLineWrap(true);
        this.console.setWrapStyleWord(true);
        this.console.setEditable(false);
        this.console.setSize(620, 400);
        this.outputPanel = new JScrollPane(this.console);
    }

    public JTextArea getConsole() {
        return console;
    }
}
