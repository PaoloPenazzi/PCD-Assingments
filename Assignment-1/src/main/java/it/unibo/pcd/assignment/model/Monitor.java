package it.unibo.pcd.assignment.model;

public class Monitor {
    private boolean pause;

    public Monitor() {
        this.pause = false;
    }

    public synchronized void pause() {
        this.pause = true;
        while (this.isPaused()) {
            try {
                wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public synchronized void play() {
        this.pause = false;
        notifyAll();
    }

    public synchronized boolean isPaused() {
        return this.pause;
    }
}
