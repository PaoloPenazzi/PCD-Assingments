package it.unibo.pcd.assignment;

import it.unibo.pcd.assignment.controller.*;
import it.unibo.pcd.assignment.view.SimulationView;

/**
 * @author aricci
 */
public class RunSimulation {

    public static void main(String[] args) {
    	SimulationView viewer = new SimulationView(620,620);
        Simulator sim = new ConcurrentSimulatorImplWithGUI(1000, 4, viewer, 2);
        //Simulator sim = new ConcurrentSimulatorImpl(1000, 5, viewer);
        long startTime = System.currentTimeMillis();
        sim.execute(50000);
        long finishTime = System.currentTimeMillis();
        System.out.println(finishTime - startTime + " ms");
    }
}