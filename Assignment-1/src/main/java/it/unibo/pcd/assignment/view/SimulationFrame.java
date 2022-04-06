package it.unibo.pcd.assignment.view;

import it.unibo.pcd.assignment.controller.ViewController;
import it.unibo.pcd.assignment.model.Body;
import it.unibo.pcd.assignment.model.Boundary;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.List;

public class SimulationFrame extends JFrame {
    private final SimulationPanel simulationPanel;

    public SimulationFrame(int width, int height, ViewController controller) {
        this.setLayout(new BorderLayout());
        setTitle("Bodies Simulation");
        setSize(width, height);
        setResizable(true);
        ControlPanel controlPanel = new ControlPanel(width, (int) (height * 0.1), controller);
        this.simulationPanel = new SimulationPanel(width, (int) (height * 0.9));
        getContentPane().add(this.simulationPanel);
        getContentPane().add(controlPanel);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent ev) {
                System.exit(-1);
            }
            public void windowClosed(WindowEvent ev) {
                System.exit(-1);
            }
        });
        this.setVisible(true);
    }


    public void display(List<Body> bodies, double virtualTime, long iteration, Boundary bounds) {
        try {
            SwingUtilities.invokeLater(() -> {
                simulationPanel.display(bodies, virtualTime, iteration, bounds);
                repaint();
            });
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void updateScale(double k) {
        this.simulationPanel.updateScale(k);
    }
}
