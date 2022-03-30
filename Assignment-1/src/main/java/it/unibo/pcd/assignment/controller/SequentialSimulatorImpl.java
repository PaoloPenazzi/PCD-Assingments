package it.unibo.pcd.assignment.controller;

public class SequentialSimulatorImpl extends AbstractSequentialSimulator {

    public SequentialSimulatorImpl(int numBodies, int numSteps, int sideLenght) {
        super(numBodies, numSteps, sideLenght);
    }

    @Override
    public void run() {
        double virtualTime = 0;
        long iteration = 0;
        while (iteration < super.getNumSteps()) {
            if(iteration % 500 == 0) {
                System.out.println("Iterazione: " + iteration);
            }
            super.computeBodies();
            virtualTime = virtualTime + DELTA_TIME;
            iteration++;
        }
    }
}
