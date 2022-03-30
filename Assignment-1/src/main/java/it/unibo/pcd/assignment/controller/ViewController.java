package it.unibo.pcd.assignment.controller;

import it.unibo.pcd.assignment.model.Body;
import it.unibo.pcd.assignment.model.Boundary;
import it.unibo.pcd.assignment.view.SimulationFrame;
import it.unibo.pcd.assignment.view.SimulationPanel;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.util.List;

public class ViewController {
    private final SimulationFrame frame;

    public ViewController(int width, int height) {
        this.frame = new SimulationFrame(width, height, this);
    }

    public void display(List<Body> bodies, double virtualTime, long iteration, Boundary bounds) {
        frame.display(bodies, virtualTime, iteration, bounds);
    }

    public void performAction(ActionEvent e) {
        JButton button = (JButton) e.getSource();
        switch (button.getText()) {
            case "START":
                break;
            case "PAUSE":
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
}
