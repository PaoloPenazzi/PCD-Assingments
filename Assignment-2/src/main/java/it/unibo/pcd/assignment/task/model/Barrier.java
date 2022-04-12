package it.unibo.pcd.assignment.task.model;

public interface Barrier {
    void waitAndNotifyAll() throws InterruptedException;
}
