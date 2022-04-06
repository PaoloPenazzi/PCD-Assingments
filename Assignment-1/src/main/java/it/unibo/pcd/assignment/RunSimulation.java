package it.unibo.pcd.assignment;

import it.unibo.pcd.assignment.controller.*;


public class RunSimulation {

    public static void main(String[] args) {
        int nWorkers = Runtime.getRuntime().availableProcessors() + 1;
<<<<<<< HEAD
        Thread simulator = new ConcurrentSimulatorImpl(100, 200000, 16, 9);
        //Thread simulator = new SequentialSimulatorImpl(1000, 10000, 8);
=======
        Thread simulator = new ConcurrentSimulatorImpl(2, 2, 8, 2);
>>>>>>> d7a016780dff06d4ffd859c349f13dcef2f41630
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