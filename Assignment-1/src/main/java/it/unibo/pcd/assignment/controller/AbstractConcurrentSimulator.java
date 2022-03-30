package it.unibo.pcd.assignment.controller;

import it.unibo.pcd.assignment.model.*;

public abstract class AbstractConcurrentSimulator extends AbstractSimulator{
    private final Worker[] workers;
    private final Barrier barrier;

    protected AbstractConcurrentSimulator(int numBodies,int numSteps, int sideLenght, int nThreads) {
        super(numBodies, numSteps, sideLenght);
        this.barrier = new BarrierImpl(nThreads);
        this.workers = new Worker[nThreads];
        this.createWorkers(nThreads);
    }

    protected void createWorkers(int nThread) {
        int bodiesPerWorker = super.getBodies().size() / nThread;
        for(int i = 0; i < nThread; i++) {
            if (i == nThread - 1) {
                this.workers[i] = new Worker(i * bodiesPerWorker, super.getBodies().size(), super.getBodies(),
                        this.barrier, super.getBounds());
            } else {
                this.workers[i] = new Worker(i * bodiesPerWorker, ((i + 1) * bodiesPerWorker),
                        super.getBodies(), this.barrier, super.getBounds());
            }
        }
    }

    public Worker[] getWorkers() {
        return workers;
    }

    public Barrier getBarrier() {
        return barrier;
    }
}
