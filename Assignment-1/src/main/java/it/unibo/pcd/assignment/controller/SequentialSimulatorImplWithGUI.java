package it.unibo.pcd.assignment.controller;

import it.unibo.pcd.assignment.model.Monitor;

public class SequentialSimulatorImplWithGUI extends AbstractSequentialSimulator {
    private final ViewController viewer;

    public SequentialSimulatorImplWithGUI(int numBodies, int numSteps, int sideLenght) {
        super(numBodies, numSteps, sideLenght);
        this.viewer = new ViewController(620, 620, new Monitor());
    }

    @Override
    public void run() {
        double virtualTime = 0;
        long iteration = 0;
        while (iteration < super.getNumSteps()) {
            super.computeBodies();
            virtualTime = virtualTime + DELTA_TIME;
            iteration++;
            this.viewer.display(getBodies(), virtualTime, iteration, getBounds());
        }
    }

}