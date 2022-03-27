package it.unibo.pcd.assignment.model;

public class BarrierImpl implements Barrier {

    private final int nWorkers;
    private int count;

    public BarrierImpl(int nWorkers) {
        this.nWorkers = nWorkers;
        this.count = 0;
    }

    @Override
    public synchronized void computeAndWaitAll() throws InterruptedException {
        this.count++;
        while (this.count < this.nWorkers) {
            wait();
        }
        notifyAll();
    }
}
