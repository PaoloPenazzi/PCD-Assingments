package it.unibo.pcd.assignment.controller;

import it.unibo.pcd.assignment.model.Worker;

public class ConcurrentSimulatorImplWithGUI extends AbstractConcurrentSimulator {
    private final ViewController view;
    private double virtualTime;
    private int iteration;

    public ConcurrentSimulatorImplWithGUI(int numBodies, int numSteps, int sideLenght, int numThread) {
        super(numBodies, numSteps, sideLenght, numThread);
        this.view = new ViewController(620, 620, super.getMonitor());
        this.virtualTime = 0;
        this.iteration = 0;
    }

    @Override
    public void run() {
        while (this.iteration < super.getNumSteps()) {
            super.getMonitor().pauseCheck();
            super.createWorkers(super.getWorkers().length);
            for (Worker worker : super.getWorkers()) {
                worker.start();
            }
            for (Worker worker : super.getWorkers()) {
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
}
