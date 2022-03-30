package it.unibo.pcd.assignment.controller;

import it.unibo.pcd.assignment.model.Body;
import it.unibo.pcd.assignment.model.Boundary;
import it.unibo.pcd.assignment.model.Position2d;
import it.unibo.pcd.assignment.model.Velocity2d;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public abstract class AbstractSimulator extends Thread {
    private final Boundary bounds;
    private final List<Body> bodies;
    private final int numSteps;
    public static final double DELTA_TIME = 0.001;

    protected AbstractSimulator(int numBodies, int numSteps, int sideLenght) {
        this.bounds = this.createField(sideLenght);
        this.bodies = this.spawnBodies(numBodies);
        this.numSteps = numSteps;
    }

    private Boundary createField(int sideLenght) {
        return new Boundary(-sideLenght, -sideLenght, sideLenght, sideLenght);
    }

    private List<Body> spawnBodies(int numBodies) {
        Random rand = new Random(System.currentTimeMillis());
        List<Body> bodyList = new ArrayList<>();
        for (int i = 0; i < numBodies; i++) {
            double x = bounds.getX0() * 0.25 + rand.nextDouble() * (bounds.getX1() - bounds.getX0()) * 0.25;
            double y = bounds.getY0() * 0.25 + rand.nextDouble() * (bounds.getY1() - bounds.getY0()) * 0.25;
            Body b = new Body(i, new Position2d(x, y), new Velocity2d(0, 0), 10);
            bodyList.add(b);
        }
        return bodyList;
    }

    public Boundary getBounds() {
        return bounds;
    }

    public List<Body> getBodies() {
        return bodies;
    }

    public int getNumSteps() {
        return numSteps;
    }
}
