package it.unibo.pcd.assignment.controller;

import it.unibo.pcd.assignment.model.Body;
import it.unibo.pcd.assignment.model.Boundary;
import it.unibo.pcd.assignment.model.Position2d;
import it.unibo.pcd.assignment.model.Velocity2d;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public abstract class AbstractConcurrentSimulator implements Simulator{
    private Boundary bounds;
    private List<Body> bodies;
    public static final double DELTA_TIME = 0.001;
    private final int numSteps;

    public AbstractConcurrentSimulator(int numBodies, int sideLenght, int numSteps) {
        this.numSteps = numSteps;
        this.createField(sideLenght);
        this.spawnBodies(numBodies);
    }

    private void createField(int sideLenght) {
        this.bounds = new Boundary(-sideLenght, -sideLenght, sideLenght, sideLenght);
    }

    private void spawnBodies(int numBodies) {
        Random rand = new Random(System.currentTimeMillis());
        this.bodies = new ArrayList<>();
        for (int i = 0; i < numBodies; i++) {
            double x = bounds.getX0() * 0.25 + rand.nextDouble() * (bounds.getX1() - bounds.getX0()) * 0.25;
            double y = bounds.getY0() * 0.25 + rand.nextDouble() * (bounds.getY1() - bounds.getY0()) * 0.25;
            Body b = new Body(i, new Position2d(x, y), new Velocity2d(0, 0), 10);
            bodies.add(b);
        }
    }

    public List<Body> getBodies() {
        return bodies;
    }

    public Boundary getBounds() {
        return bounds;
    }

    public int getNumSteps() {
        return numSteps;
    }
}
