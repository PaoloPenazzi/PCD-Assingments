package it.unibo.pcd.assignment.controller;

import it.unibo.pcd.assignment.model.Barrier;
import it.unibo.pcd.assignment.model.BarrierImpl;
import it.unibo.pcd.assignment.model.Worker;

public class ConcurrentSimulatorImplWithGUI extends AbstractConcurrentSimulator {
    private final int nWorkers;
    private final Barrier barrier;
    private final Worker[] workers;
    private final ViewController view;
    private double virtualTime;
    private int iteration;

    public ConcurrentSimulatorImplWithGUI(int numBodies, int sideLenght, int numSteps) {
        super(numBodies, sideLenght, numSteps);
        this.view = new ViewController(620, 620, this);
        this.nWorkers = Runtime.getRuntime().availableProcessors() + 1;
        this.workers = new Worker[nWorkers];
        this.barrier = new BarrierImpl(this.nWorkers);
        this.createWorkers();
        this.virtualTime = 0;
        this.iteration = 0;
    }

    public ConcurrentSimulatorImplWithGUI(int numBodies, int sideLenght, int numSteps, int numThread) {
        super(numBodies, sideLenght, numSteps);
        this.view = new ViewController(620, 620, this);
        this.nWorkers = numThread;
        this.workers = new Worker[nWorkers];
        this.barrier = new BarrierImpl(this.nWorkers);
        this.createWorkers();
        this.virtualTime = 0;
        this.iteration = 0;
    }

    @Override
    public void execute() {
        while (this.iteration < super.getNumSteps() && !isPaused()) {
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
            this.virtualTime = this.virtualTime + DELTA_TIME;
            this.iteration++;
            view.display(super.getBodies(), this.virtualTime, this.iteration, super.getBounds());
        }
    }

    private boolean isPaused() {
        return !view.getIsRunning();
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
