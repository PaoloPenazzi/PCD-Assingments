package it.unibo.pcd.assignment;

import it.unibo.pcd.assignment.controller.Simulator;
import it.unibo.pcd.assignment.controller.SimulatorExample;
import it.unibo.pcd.assignment.controller.SimulatorWithGUI;
import it.unibo.pcd.assignment.view.SimulationView;

/**
 * Bodies simulation - legacy code: sequential, unstructured
 * 
 * @author aricci
 */
public class SequentialBodySimulationMain {

    public static void main(String[] args) {
                
    	SimulationView viewer = new SimulationView(620,620);

    	Simulator sim = new SimulatorWithGUI(1000, 4, viewer);
        sim.execute(2000);
    }
}
