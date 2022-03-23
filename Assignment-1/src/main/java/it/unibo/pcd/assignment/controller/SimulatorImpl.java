package it.unibo.pcd.assignment.controller;

import it.unibo.pcd.assignment.model.Body;

public class SimulatorImpl extends AbstractSimulator{

    public SimulatorImpl(int numBodies, int sideLenght) {
        super(numBodies, sideLenght);
    }

    @Override
    public void execute(int numSteps) {
        double virtualTime = 0;
        long iteration = 0;
        while (iteration < numSteps) {
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
