package it.unibo.pcd.assignment;

import it.unibo.pcd.assignment.controller.*;


public class RunSimulation {

    public static void main(String[] args) {
        int nWorkers = Runtime.getRuntime().availableProcessors() + 1;
        Thread simulator = new ConcurrentSimulatorImpl(2, 2, 8, 2);
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