package it.unibo.pcd.assignment.controller;

import it.unibo.pcd.assignment.model.Body;
import it.unibo.pcd.assignment.model.Boundary;
import it.unibo.pcd.assignment.view.SimulationFrame;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.util.List;

public class ViewController {
    private final SimulationFrame frame;
    private boolean isRunning = true;
    private final Simulator simulator;

    public ViewController(int width, int height, Simulator simulator) {
        this.frame = new SimulationFrame(width, height, this);
        this.simulator = simulator;
    }

    public void display(List<Body> bodies, double virtualTime, long iteration, Boundary bounds) {
        frame.display(bodies, virtualTime, iteration, bounds);
    }

    public void actionPerformed(ActionEvent e) {
        JButton button = (JButton) e.getSource();
        switch (button.getText()) {
            case "PLAY":
                if(!this.isRunning) {
                    this.isRunning = true;
                    this.simulator.execute();
                }
                break;
            case "PAUSE":
                this.isRunning = false;
                break;
            case "+":
                this.frame.updateScale(1.1);
                break;
            case "-":
                this.frame.updateScale(0.9);
                break;
            default:
                throw new IllegalStateException();
        }
    }

    public boolean getIsRunning() {
        return this.isRunning;
    }
}
