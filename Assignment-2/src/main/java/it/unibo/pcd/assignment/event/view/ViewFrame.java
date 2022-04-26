package it.unibo.pcd.assignment.event.view;

import javax.swing.*;
import java.awt.*;

public class ViewFrame extends JFrame {
    private JButton openProject;
    private JTextArea console;
    private JPanel controlPanel;

    public ViewFrame() {
        this.setSize(620, 620);
        this.setTitle("Project Analyzer");
        this.setLayout(new BorderLayout());
        this.createControlPanel();
        this.createOutputPanel();
        this.getContentPane().add(BorderLayout.NORTH, this.controlPanel);
        this.getContentPane().add(BorderLayout.CENTER, this.console);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setVisible(true);
    }

    private void createControlPanel() {
        this.controlPanel = new JPanel();
        this.openProject = new JButton("Open Project");
        this.controlPanel.add(this.openProject);
    }

    private void createOutputPanel() {
        this.console = new JTextArea();
        this.console.setLineWrap(true);
        this.console.setWrapStyleWord(true);
        this.console.setEditable(false);
        this.console.setSize(620, 400);
        this.console.append("START");
    }
}
