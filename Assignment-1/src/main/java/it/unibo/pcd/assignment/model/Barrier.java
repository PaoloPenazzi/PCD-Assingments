package it.unibo.pcd.assignment.model;

public interface Barrier {
    void waitAndNotifyAll() throws InterruptedException;
}
