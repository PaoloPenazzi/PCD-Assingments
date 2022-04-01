package it.unibo.pcd.assignment.controller;

import it.unibo.pcd.assignment.model.*;

import java.util.concurrent.CountDownLatch;

public abstract class AbstractConcurrentSimulator extends AbstractSimulator{
    private final Worker[] workers;
    private final Barrier barrier;
    private final Monitor monitor;
    private CountDownLatch latch;

    protected AbstractConcurrentSimulator(int numBodies,int numSteps, int sideLenght, int nThreads) {
        super(numBodies, numSteps, sideLenght);
        this.barrier = new BarrierImpl(nThreads);
        this.workers = new Worker[nThreads];
        this.monitor = new Monitor();
        this.createWorkers(nThreads);
    }

    protected void createWorkers(int nThread) {
        int bodiesPerWorker = super.getBodies().size() / nThread;
        for(int i = 0; i < nThread; i++) {
            if (i == nThread - 1) {
                this.workers[i] = new Worker(i * bodiesPerWorker, super.getBodies().size(), super.getBodies(),
                        this.barrier, super.getBounds(), this.latch);
            } else {
                this.workers[i] = new Worker(i * bodiesPerWorker, ((i + 1) * bodiesPerWorker),
                        super.getBodies(), this.barrier, super.getBounds(), this.latch);
            }
        }
    }

    protected void createLatch() {
        this.latch = new CountDownLatch(this.workers.length);
    }

    public CountDownLatch getLatch() {
        return this.latch;
    }

    public Worker[] getWorkers() {
        return this.workers;
    }

    public Barrier getBarrier() {
        return this.barrier;
    }

    public Monitor getMonitor() {
        return this.monitor;
    }
}
