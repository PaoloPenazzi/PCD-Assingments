package it.unibo.pcd.assignment.controller;

import it.unibo.pcd.assignment.model.Worker;

public class ConcurrentSimulatorImpl extends AbstractConcurrentSimulator {

    public ConcurrentSimulatorImpl(int numBodies, int numSteps, int sideLenght, int nThreads) {
        super(numBodies, numSteps, sideLenght, nThreads);
        System.out.println("Workers number: " + super.getWorkers().length);
    }

    @Override
    public void run() {
        long iteration = 0;
        while (iteration < super.getNumSteps()) {
            this.createWorkers(super.getWorkers().length);
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
            iteration++;
        }
    }
}
