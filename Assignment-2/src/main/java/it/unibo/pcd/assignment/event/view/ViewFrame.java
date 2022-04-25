package it.unibo.pcd.assignment.event.view;

import javax.swing.*;
import java.awt.*;

public class ViewFrame extends JFrame {
    private JButton openProject;
    private JTextArea console;
    private JPanel controlPanel;
    private JPanel outputPanel;


    public ViewFrame() {
        this.setSize(1200, 720);
        this.setTitle("Project Analyzer");
        this.setLayout(new BorderLayout());
        this.createControlPanel();
        this.createOutputPanel();
        this.getContentPane().add(this.controlPanel, BorderLayout.NORTH);
        this.getContentPane().add(this.outputPanel, BorderLayout.SOUTH);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setVisible(true);
    }

    private void createControlPanel() {
        this.controlPanel = new JPanel();
        this.openProject = new JButton("Open Project");
        this.controlPanel.add(this.openProject);
    }

    private void createOutputPanel() {
        this.outputPanel = new JPanel();
        this.console = new JTextArea();
        this.console.setSize(1200, 400);
        this.outputPanel.add(this.console);
        this.outputPanel.setSize(1200,400);
        this.console.append("START");
    }
}
