package it.unibo.pcd.assignment.controller;

import it.unibo.pcd.assignment.model.Body;
import it.unibo.pcd.assignment.model.Boundary;
import it.unibo.pcd.assignment.model.Position2d;
import it.unibo.pcd.assignment.model.Velocity2d;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public abstract class AbstractSequentialSimulator extends AbstractSimulator {

    protected AbstractSequentialSimulator(int numBodies,int numSteps, int sideLenght) {
        super(numBodies, numSteps, sideLenght);
    }

    protected void computeBodies() {
        this.computeBodiesVelocity();
        this.computeBodiesPosition();
        this.computeBodiesCollision();
    }

    private void computeBodiesVelocity() {
        for (Body b : super.getBodies()) {
            Velocity2d totalForce = computeTotalForceOnBody(b);
            Velocity2d acceleration = new Velocity2d(totalForce).scalarMul(1.0 / b.getMass());
            b.updateVelocity(acceleration, DELTA_TIME);
        }
    }

    private void computeBodiesPosition() {
        for (Body b : super.getBodies()) {
            b.updatePos(DELTA_TIME);
        }
    }

    private void computeBodiesCollision() {
        for (Body b : super.getBodies()) {
            b.checkAndSolveBoundaryCollision(super.getBounds());
        }
    }

    private Velocity2d computeTotalForceOnBody(Body b) {
        Velocity2d totalForce = new Velocity2d(0, 0);
        for (Body otherBody : super.getBodies()) {
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
