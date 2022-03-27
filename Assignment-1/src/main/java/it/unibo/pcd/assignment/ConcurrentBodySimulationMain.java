package it.unibo.pcd.assignment;

import it.unibo.pcd.assignment.controller.ConcurrentSimulatorImpl;
import it.unibo.pcd.assignment.controller.Simulator;

public class ConcurrentBodySimulationMain {
    public static void main(String[] args) {
        new ConcurrentSimulatorImpl(1000, 16).execute(1);
    }
}
