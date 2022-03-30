package it.unibo.pcd.assignment.model;

import it.unibo.pcd.assignment.controller.AbstractConcurrentSimulator;
import it.unibo.pcd.assignment.controller.ConcurrentSimulatorImplWithGUI;

import java.util.List;
import java.util.Objects;

public class Worker extends Thread {
    private final List<Body> allBodies;
    private final Barrier barrier;
    private final Boundary boundary;
    private final int indexFrom;
    private final int indexTo;

    public Worker(int indexFrom, int indexTo, List<Body> bodies, Barrier barrier, Boundary boundary) {
        this.indexFrom = indexFrom;
        this.indexTo = indexTo;
        this.allBodies = bodies;
        this.barrier = barrier;
        this.boundary = boundary;
    }

    @Override
    public void run() {
        try {
            this.computeBodiesVelocity();
            this.barrier.waitAndNotifyAll();
            updatePositionAndCheckCollision();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void log(String message){
        synchronized(System.out){
            System.out.println("[Worker: " + message);
        }
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

    private void updatePositionAndCheckCollision() {
        for (Body body : this.allBodies.subList(indexFrom, indexTo)) {
            body.updatePos(AbstractConcurrentSimulator.DELTA_TIME);
            body.checkAndSolveBoundaryCollision(boundary);
        }
    }
}
