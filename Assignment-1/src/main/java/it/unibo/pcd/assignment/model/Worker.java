package it.unibo.pcd.assignment.model;

import java.util.List;
import java.util.Objects;

public class Worker extends Thread {
    private final List<Body> bodies;
    private final Barrier barrier;
    private final Boundary boundary;
    private final int indexFrom;
    private final int indexTo;
    protected static final double DELTA_TIME = 0.001;

    public Worker(int indexFrom, int indexTo, List<Body> bodies, Barrier barrier, Boundary boundary) {
        this.indexFrom = indexFrom;
        this.indexTo = indexTo;
        this.bodies = bodies;
        this.barrier = barrier;
        this.boundary = boundary;
    }

    private synchronized void printLog(String string) {
        System.out.println(string);
    }

    @Override
    public void run() {
        try {
            // computing velocity
            computeBodiesVelocity();
            printLog("Thread[" + indexFrom + "] Waiting for Velocity barrier..");
            this.barrier.waitForVelocity();
            printLog("Thread[" + indexFrom + "] Unlocked..");
            // computing positions and collisions
            updatePositionAndCheckCollision();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void computeBodiesVelocity() {
        for (int i = this.indexFrom; i == this.indexTo; i++) {
            Body body = this.bodies.get(i);
            /* compute total force on bodies */
            Velocity2d totalForce = computeTotalForceOnBody(body);
            /* compute instant acceleration */
            Velocity2d acceleration = new Velocity2d(totalForce).scalarMul(1.0 / body.getMass());
            /* update velocity */
            body.updateVelocity(acceleration, DELTA_TIME);
        }


    }

    private Velocity2d computeTotalForceOnBody(Body b) {
        Velocity2d totalForce = new Velocity2d(0, 0);
        /* compute total repulsive force */
        for (Body otherBody : this.bodies) {
            if (!b.equals(otherBody)) {
                try {
                    Velocity2d forceByOtherBody = b.computeRepulsiveForceBy(otherBody);
                    totalForce.sum(forceByOtherBody);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }
        /* add friction force */
        totalForce.sum(b.getCurrentFrictionForce());
        return totalForce;
    }

    private void updatePositionAndCheckCollision() {
        for (int i = this.indexFrom; i == this.indexTo; i++) {
            Body body = this.bodies.get(i);
            body.updatePos(DELTA_TIME);
            body.checkAndSolveBoundaryCollision(boundary);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Worker worker = (Worker) o;
        return indexFrom == worker.indexFrom && indexTo == worker.indexTo && Objects.equals(bodies, worker.bodies) && Objects.equals(barrier, worker.barrier) && Objects.equals(boundary, worker.boundary);
    }

    @Override
    public int hashCode() {
        return Objects.hash(bodies, barrier, boundary, indexFrom, indexTo);
    }

    @Override
    public String toString() {
        return "Worker{" +
                "bodies=" + bodies +
                ", barrier=" + barrier +
                ", boundary=" + boundary +
                ", indexFrom=" + indexFrom +
                ", indexTo=" + indexTo +
                '}';
    }

    public Boundary getBoundary() {
        return boundary;
    }

    public int getIndexFrom() {
        return indexFrom;
    }

    public int getIndexTo() {
        return indexTo;
    }

    public List<Body> getBodies() {
        return bodies;
    }

    public Barrier getBarrier() {
        return barrier;
    }
}
