package it.unibo.pcd.assignment.model;

public class BarrierImpl implements Barrier {
    private final int nWorkers;
    private int count;

    public BarrierImpl(int nWorkers) {
        this.nWorkers = nWorkers;
        this.count = 0;
    }

    @Override
    public synchronized void waitAndNotifyAll() throws InterruptedException {
        count++;
        if (count == nWorkers) {
            notifyAll();
        } else {
            while (count < nWorkers) {
                wait();
            }
        }
    }
}
