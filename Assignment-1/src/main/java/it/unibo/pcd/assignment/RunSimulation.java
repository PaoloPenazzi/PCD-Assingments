package it.unibo.pcd.assignment;

import it.unibo.pcd.assignment.controller.*;
import it.unibo.pcd.assignment.view.SimulationView;

/**
 * @author aricci
 */
public class RunSimulation {

    public static void main(String[] args) {
    	SimulationView viewer = new SimulationView(620,620);
        Simulator sim = new SequentialSimulatorImplWithGUI(100, 4, viewer);
        long startTime = System.currentTimeMillis();
        sim.execute(50000);
        long finishTime = System.currentTimeMillis();
        System.out.println(finishTime - startTime + " ms");
    }
}