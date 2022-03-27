package it.unibo.pcd.assignment;

import it.unibo.pcd.assignment.controller.ConcurrentSimulatorImpl;

public class ConcurrentBodySimulationMain {
    public static void main(String[] args) {
        new ConcurrentSimulatorImpl(1000, 16).execute(10000);
    }
}
