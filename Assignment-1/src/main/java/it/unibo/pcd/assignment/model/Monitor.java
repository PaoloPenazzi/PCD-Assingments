package it.unibo.pcd.assignment.model;

public class Monitor {
    private boolean pause;

    public Monitor() {
        this.pause = false;
    }

    public synchronized void waitPauseTrue() {
        while (isPause()) {
            try {
                this.wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public synchronized void setPauseState(boolean state) {
        this.pause = state;
        this.notifyAll();
    }

    public boolean isPause() {
        return this.pause;
    }
}
