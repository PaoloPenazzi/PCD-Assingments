package it.unibo.pcd.assignment.controller;

import it.unibo.pcd.assignment.model.Body;
import it.unibo.pcd.assignment.model.Boundary;
import it.unibo.pcd.assignment.model.Monitor;
import it.unibo.pcd.assignment.view.SimulationFrame;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.util.List;

public class ViewController {
    private final SimulationFrame frame;
    private final Monitor pause;

    public ViewController(int width, int height, Monitor pause) {
        this.frame = new SimulationFrame(width, height, this);
        this.pause = pause;
    }

    public void display(List<Body> bodies, double virtualTime, long iteration, Boundary bounds) {
        frame.display(bodies, virtualTime, iteration, bounds);
    }

    public void actionPerformed(ActionEvent e) {
        JButton button = (JButton) e.getSource();
        switch (button.getText()) {
            case "PLAY":
                synchronized (this) {
                    this.pause.play();
                }
                break;
            case "PAUSE":
                synchronized (this) {
                    this.pause.pause();
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
