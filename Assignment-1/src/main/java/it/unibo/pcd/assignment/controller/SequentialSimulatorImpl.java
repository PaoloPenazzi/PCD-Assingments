package it.unibo.pcd.assignment.controller;

import it.unibo.pcd.assignment.model.Body;

public class SequentialSimulatorImpl extends AbstractSequentialSimulator {

    public SequentialSimulatorImpl(int numBodies, int sideLenght, int numSteps) {
        super(numBodies, sideLenght, numSteps);
    }

    @Override
    public void execute() {
        double virtualTime = 0;
        long iteration = 0;
        while (iteration < super.getNumSteps()) {
            if(iteration % 500 == 0) {
                System.out.println("Iterazione: " + iteration);
            }
            super.computeBodiesVelocity();
            for (Body b : super.getBodies()) {
                b.updatePos(DELTA_TIME);
            }
            for (Body b : super.getBodies()) {
                b.checkAndSolveBoundaryCollision(super.getBounds());
            }
            virtualTime = virtualTime + DELTA_TIME;
            iteration++;
        }
    }
}
