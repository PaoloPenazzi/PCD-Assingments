package it.unibo.pcd.assignment.model;

public class Monitor {
    private int i;

    public Monitor() {
        this.i = 0;
    }

    public synchronized void pause() {
        this.i = 1;
    }

    public synchronized void play() {
        this.i = 0;
    }

    public synchronized boolean isPaused() {
        return this.i == 1;
    }
}
