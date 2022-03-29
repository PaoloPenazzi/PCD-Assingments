package it.unibo.pcd.assignment.model;

import java.util.List;
import java.util.Objects;

public class Worker extends Thread {
    private final List<Body> allBodies;
    private final List<Body> myBodies;
    private final Barrier barrier;
    private final Boundary boundary;
    private final int indexFrom;
    private final int indexTo;
    protected static final double DELTA_TIME = 0.001;

    public Worker(int indexFrom, int indexTo, List<Body> bodies, Barrier barrier, Boundary boundary) {
        this.indexFrom = indexFrom;
        this.indexTo = indexTo;
        this.allBodies = bodies;
        this.barrier = barrier;
        this.boundary = boundary;
        this.myBodies = bodies.subList(indexFrom, indexTo);
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

    private void computeBodiesVelocity() {
        for (Body body : this.myBodies) {
            Velocity2d totalForce = computeTotalForceOnBody(body);
            Velocity2d acceleration = new Velocity2d(totalForce).scalarMul(1.0 / body.getMass());
            body.updateVelocity(acceleration, DELTA_TIME);
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
        for (int i = this.indexFrom; i == this.indexTo; i++) {
            Body body = this.allBodies.get(i);
            body.updatePos(DELTA_TIME);
            body.checkAndSolveBoundaryCollision(boundary);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Worker worker = (Worker) o;
        return indexFrom == worker.indexFrom && indexTo == worker.indexTo && Objects.equals(allBodies, worker.allBodies) && Objects.equals(barrier, worker.barrier) && Objects.equals(boundary, worker.boundary);
    }

    @Override
    public int hashCode() {
        return Objects.hash(allBodies, barrier, boundary, indexFrom, indexTo);
    }

    @Override
    public String toString() {
        return "Worker{" +
                "bodies=" + allBodies +
                ", barrier=" + barrier +
                ", boundary=" + boundary +
                ", indexFrom=" + indexFrom +
                ", indexTo=" + indexTo +
                '}';
    }

    public int getIndexFrom() {
        return indexFrom;
    }

    public int getIndexTo() {
        return indexTo;
    }
}
