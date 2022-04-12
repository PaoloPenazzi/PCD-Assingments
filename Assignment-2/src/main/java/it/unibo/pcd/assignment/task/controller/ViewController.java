package it.unibo.pcd.assignment.task.controller;

import it.unibo.pcd.assignment.task.model.Body;
import it.unibo.pcd.assignment.task.model.Boundary;
import it.unibo.pcd.assignment.task.model.Monitor;
import it.unibo.pcd.assignment.task.view.SimulationFrame;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.util.List;

public class ViewController {
    private final SimulationFrame frame;
    private final Monitor monitor;

    public ViewController(int width, int height, Monitor monitor) {
        this.frame = new SimulationFrame(width, height, this);
        this.monitor = monitor;
    }

    public void display(List<Body> bodies, double virtualTime, long iteration, Boundary bounds) {
        frame.display(bodies, virtualTime, iteration, bounds);
    }

    public void actionPerformed(ActionEvent e) {
        JButton button = (JButton) e.getSource();
        switch (button.getText()) {
            case "PLAY":
                synchronized (this) {
                    this.monitor.setPauseState(false);
                }
                break;
            case "PAUSE":
                synchronized (this) {
                    this.monitor.setPauseState(true);
                }
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
