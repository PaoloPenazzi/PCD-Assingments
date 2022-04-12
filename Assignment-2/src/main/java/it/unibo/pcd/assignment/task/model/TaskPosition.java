package it.unibo.pcd.assignment.task.model;

import it.unibo.pcd.assignment.task.controller.AbstractConcurrentSimulator;

import java.util.List;
import java.util.concurrent.Callable;

public class TaskPosition implements Callable<Void> {
    private final List<Body> allBodies;
    private final int indexFrom;
    private final int indexTo;
    private final Boundary boundary;
    
    public TaskPosition(int indexFrom, int indexTo, List<Body> bodyList, Boundary boundary) {
        this.indexFrom = indexFrom;
        this.indexTo = indexTo;
        this.allBodies = bodyList;
        this.boundary = boundary;
    }

    @Override
    public Void call() throws Exception {
        this.updatePositionAndCheckCollision();
        return null;
    }

    private void updatePositionAndCheckCollision() {
        for (Body body : this.allBodies.subList(indexFrom, indexTo)) {
            body.updatePos(AbstractConcurrentSimulator.DELTA_TIME);
            body.checkAndSolveBoundaryCollision(boundary);
        }
    }
}
