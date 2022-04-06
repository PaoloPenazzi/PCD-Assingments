package it.unibo.pcd.assignment;

import it.unibo.pcd.assignment.controller.*;

public class RunSimulation {

    public static void main(String[] args) {
        int nWorkers = Runtime.getRuntime().availableProcessors() + 1;
        Thread simulator = new ConcurrentSimulatorImplWithGUI(1000, 60000, 8, 4);
        long startTime = System.currentTimeMillis();
        simulator.start();
        try {
            simulator.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        long finishTime = System.currentTimeMillis();
        System.out.println(finishTime - startTime + " ms");
    }
}