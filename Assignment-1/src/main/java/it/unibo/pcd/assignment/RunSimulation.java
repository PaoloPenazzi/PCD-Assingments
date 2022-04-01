package it.unibo.pcd.assignment;

import it.unibo.pcd.assignment.controller.*;
import it.unibo.pcd.assignment.model.Monitor;

/**
 * @author aricci
 */
public class RunSimulation {

    public static void main(String[] args) {
        int nWorkers = Runtime.getRuntime().availableProcessors() + 1;
        Thread simulator = new ConcurrentSimulatorImplWithGUI(1000, 10000, 8, nWorkers);
        //Thread simulator = new SequentialSimulatorImpl(1000, 10000, 8);
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