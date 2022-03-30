package it.unibo.pcd.assignment;

import it.unibo.pcd.assignment.controller.*;
import it.unibo.pcd.assignment.view.BaseView;
import it.unibo.pcd.assignment.view.SimulationFrame;

/**
 * @author aricci
 */
public class RunSimulation {

    public static void main(String[] args) {
    	// BaseView viewer = new BaseView(620,620);
        Simulator sim = new ConcurrentSimulatorImplWithGUI(1000, 8, 10000);
        long startTime = System.currentTimeMillis();
        sim.execute();
        long finishTime = System.currentTimeMillis();
        System.out.println(finishTime - startTime + " ms");
    }
}