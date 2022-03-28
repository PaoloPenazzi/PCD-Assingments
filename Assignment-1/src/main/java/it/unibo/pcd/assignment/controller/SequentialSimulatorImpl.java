package it.unibo.pcd.assignment.controller;

import it.unibo.pcd.assignment.model.Body;

public class SequentialSimulatorImpl extends AbstractSequentialSimulator {

    public SequentialSimulatorImpl(int numBodies, int sideLenght) {
        super(numBodies, sideLenght);
    }

    @Override
    public void execute(int numSteps) {
        double virtualTime = 0;
        long iteration = 0;
        while (iteration < numSteps) {
            if(iteration % 500 == 0) {
                System.out.println("Iterazione: " + iteration);
            }
            super.computeBodiesVelocity();
            // Compute bodies' new position.
            for (Body b : super.getBodies()) {
                b.updatePos(DELTA_TIME);
            }
            for (Body b : super.getBodies()) {
                b.checkAndSolveBoundaryCollision(super.getBounds());
            }
            /* update virtual time */
            virtualTime = virtualTime + DELTA_TIME;
            iteration++;
        }
    }
}
