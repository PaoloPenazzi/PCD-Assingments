package it.unibo.pcd.assignment.controller;

import it.unibo.pcd.assignment.model.*;

import java.util.concurrent.CountDownLatch;

public abstract class AbstractConcurrentSimulator extends AbstractSimulator {
    private final Worker[] workers;
    private Barrier barrier;
    private CountDownLatch latch;
    private final int nThreads;
    private final Monitor monitor;

    protected AbstractConcurrentSimulator(int numBodies, int numSteps, int sideLenght, int nThreads) {
        super(numBodies, numSteps, sideLenght);
        this.monitor = new Monitor();
        this.workers = new Worker[nThreads];
        this.nThreads = nThreads;
    }

    protected void createWorkers() {
        this.barrier = new BarrierImpl(nThreads);
        int bodiesPerWorker = super.getBodies().size() / this.nThreads;
        for (int i = 0; i < this.nThreads; i++) {
            if (i == this.nThreads - 1) {
                this.workers[i] = new Worker(i * bodiesPerWorker, super.getBodies().size(), super.getBodies(),
                        this.barrier, super.getBounds(), this.latch);
            } else {
                this.workers[i] = new Worker(i * bodiesPerWorker, ((i + 1) * bodiesPerWorker),
                        super.getBodies(), this.barrier, super.getBounds(), this.latch);
            }
        }
    }

    public void createLatch() {
        this.latch = new CountDownLatch(this.nThreads);
    }

    public CountDownLatch getLatch() {
        return this.latch;
    }

    public Worker[] getWorkers() {
        return this.workers;
    }

    public Monitor getMonitor() {return this.monitor;}

    public Barrier getBarrier() {return this.barrier;}
}
