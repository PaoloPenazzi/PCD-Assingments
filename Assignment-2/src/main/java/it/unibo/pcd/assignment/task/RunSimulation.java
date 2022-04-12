package it.unibo.pcd.assignment.task;

import it.unibo.pcd.assignment.task.controller.*;

public class RunSimulation {

    public static void main(String[] args) {
        int nWorkers = Runtime.getRuntime().availableProcessors() + 1;
        Thread simulator = new MasterWithGUI(1000, 10000, 16, 10);
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