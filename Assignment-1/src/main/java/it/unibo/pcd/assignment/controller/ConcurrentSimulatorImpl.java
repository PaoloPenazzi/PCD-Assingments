package it.unibo.pcd.assignment.controller;

import it.unibo.pcd.assignment.model.Barrier;
import it.unibo.pcd.assignment.model.BarrierImpl;
import it.unibo.pcd.assignment.model.Worker;

public class ConcurrentSimulatorImpl extends AbstractConcurrentSimulator {
    private final int nWorkers;
    private final Barrier barrier;
    private final Worker[] workers;

    public ConcurrentSimulatorImpl(int numBodies, int sideLenght, int numSteps) {
        super(numBodies, sideLenght, numSteps);
        this.nWorkers = Runtime.getRuntime().availableProcessors() + 1;
        System.out.println("Workers number: " + this.nWorkers);
        this.workers = new Worker[nWorkers];
        this.barrier = new BarrierImpl(this.nWorkers);
        this.createWorkers();
    }

    @Override
    public void execute() {
        long iteration = 0;
        while (iteration < getNumSteps()) {
            this.createWorkers();
            for (Worker worker : this.workers) {
                worker.start();
            }
            for (Worker worker : this.workers) {
                try {
                    worker.join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            iteration++;
        }
    }

    public void createWorkers() {
        int bodiesPerWorker = super.getBodies().size() / this.nWorkers;
        for(int i = 0; i < this.nWorkers; i++) {
            if (i == this.nWorkers - 1) {
                this.workers[i] = new Worker(i * bodiesPerWorker, super.getBodies().size(), super.getBodies(),
                        this.barrier, super.getBounds());
            } else {
                this.workers[i] = new Worker(i * bodiesPerWorker, ((i + 1) * bodiesPerWorker),
                        super.getBodies(), this.barrier, super.getBounds());
            }
        }
    }
}
