package it.unibo.pcd.assignment;

import it.unibo.pcd.assignment.controller.*;
import it.unibo.pcd.assignment.view.SimulationView;

/**
 * Bodies simulation - legacy code: sequential, unstructured
 * 
 * @author aricci
 */
public class RunSimulation {

    public static void main(String[] args) {
    	// SimulationView viewer = new SimulationView(620,620);
        //SimulatorExample sim = new SimulatorExample();
        // Simulator sim = new SimulatorImplWithGUI(1000, 6, viewer);
        Simulator sim = new ConcurrentSimulatorImpl(1000, 5);
        long startTime = System.currentTimeMillis();
        sim.execute(1000);
        long finishTime = System.currentTimeMillis();
        System.out.println(finishTime - startTime + " ms");
    }
}