package it.unibo.pcd.assignment.model;

public interface Barrier {
    void waitForVelocity() throws InterruptedException;

    void resetCounter();
}
