package it.unibo.pcd.assignment.model;

import java.util.List;

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

    @Override
    public void run() {
        super.run();
        // computare la velocità
        computeBodiesVelocity();

        // barriera
        try {
            this.barrier.computeAndWaitAll();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // computare posizione e collisioni
        updatePositionAndCheckCollision();
    }

    public List<Body> getBodies() {
        return bodies;
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
}
