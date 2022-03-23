package it.unibo.pcd.assignment;

import it.unibo.pcd.assignment.controller.Simulator;
import it.unibo.pcd.assignment.controller.SimulatorExample;
import it.unibo.pcd.assignment.controller.SimulatorImpl;
import it.unibo.pcd.assignment.controller.SimulatorImplWithGUI;
import it.unibo.pcd.assignment.view.SimulationView;

/**
 * Bodies simulation - legacy code: sequential, unstructured
 * 
 * @author aricci
 */
public class SequentialBodySimulationMain {

    public static void main(String[] args) {
    	// SimulationView viewer = new SimulationView(620,620);
        // SimulatorExample sim = new SimulatorExample();
        Simulator sim = new SimulatorImpl(5000, 6);
        long startTime = System.currentTimeMillis();
        sim.execute(50000);
        long finishTime = System.currentTimeMillis();
        System.out.println(finishTime - startTime + " ms");
    }
}
