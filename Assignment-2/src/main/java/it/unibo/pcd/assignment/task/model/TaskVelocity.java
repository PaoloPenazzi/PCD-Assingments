package it.unibo.pcd.assignment.task.model;

import it.unibo.pcd.assignment.task.controller.AbstractConcurrentSimulator;

import java.util.List;
import java.util.concurrent.Callable;

public class TaskVelocity implements Callable<Void> {
    private final List<Body> allBodies;
    private final int indexFrom;
    private final int indexTo;

    public TaskVelocity(int indexFrom, int indexTo, List<Body> bodyList) {
        this.indexFrom = indexFrom;
        this.indexTo = indexTo;
        this.allBodies = bodyList;
    }

    @Override
    public Void call() throws Exception {
        this.computeBodiesVelocity();
        System.out.println("Velocity update done.");
        return null;
    }

    private void computeBodiesVelocity() {
        for (Body body : this.allBodies.subList(indexFrom, indexTo)) {
            Velocity2d totalForce = computeTotalForceOnBody(body);
            Velocity2d acceleration = new Velocity2d(totalForce).scalarMul(1.0 / body.getMass());
            body.updateVelocity(acceleration, AbstractConcurrentSimulator.DELTA_TIME);
        }
    }

    private Velocity2d computeTotalForceOnBody(Body b) {
        Velocity2d totalForce = new Velocity2d(0, 0);
        for (Body otherBody : this.allBodies) {
            if (!b.equals(otherBody)) {
                try {
                    Velocity2d forceByOtherBody = b.computeRepulsiveForceBy(otherBody);
                    totalForce.sum(forceByOtherBody);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }
        totalForce.sum(b.getCurrentFrictionForce());
        return totalForce;
    }
}
