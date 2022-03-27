package it.unibo.pcd.assignment.model;

public interface Barrier {
    void computeAndWaitAll() throws InterruptedException;

    void resetCounter();
}
