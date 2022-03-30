package it.unibo.pcd.assignment;

import it.unibo.pcd.assignment.controller.*;
import it.unibo.pcd.assignment.view.BaseView;
import it.unibo.pcd.assignment.view.SimulationFrame;

/**
 * @author aricci
 */
public class RunSimulation {

    public static void main(String[] args) {
        SimulationFrame view = new SimulationFrame(620, 620);
    	//BaseView viewer = new BaseView(620,620);
        Simulator sim = new SequentialSimulatorImplWithGUI(1000, 4, view);
        long startTime = System.currentTimeMillis();
        sim.execute(50000);
        long finishTime = System.currentTimeMillis();
        System.out.println(finishTime - startTime + " ms");
    }
}