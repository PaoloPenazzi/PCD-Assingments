package it.unibo.pcd.assignment.controller;

import it.unibo.pcd.assignment.model.Barrier;
import it.unibo.pcd.assignment.model.BarrierImpl;
import it.unibo.pcd.assignment.model.Body;
import it.unibo.pcd.assignment.model.Worker;
import it.unibo.pcd.assignment.view.SimulationView;

public class ConcurrentSimulatorImplWithGUI extends AbstractConcurrentSimulator {
    private final int nWorkers;
    private final Barrier barrier;
    private final Worker[] workers;
    private final SimulationView view;
    public static final double DELTA_TIME = 0.001;

    public ConcurrentSimulatorImplWithGUI(int numBodies, int sideLenght, SimulationView view) {
        super(numBodies, sideLenght);
        this.view = view;
        this.nWorkers = Runtime.getRuntime().availableProcessors() + 1;
        this.workers = new Worker[nWorkers];
        this.barrier = new BarrierImpl(this.nWorkers);
        this.createWorkers();
    }

    public ConcurrentSimulatorImplWithGUI(int numBodies, int sideLenght, SimulationView view, int numThread) {
        super(numBodies, sideLenght);
        this.view = view;
        this.nWorkers = numThread;
        this.workers = new Worker[nWorkers];
        this.barrier = new BarrierImpl(this.nWorkers);
        this.createWorkers();
    }

    @Override
    public void execute(int numSteps) {
        double virtualTime = 0;
        long iteration = 0;
        while (iteration < numSteps) {
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
            virtualTime = virtualTime + DELTA_TIME;
            iteration++;
            view.display(super.getBodies(), virtualTime, iteration, super.getBounds());
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
