package it.unibo.pcd.assignment.controller;

import it.unibo.pcd.assignment.model.Worker;

public class ConcurrentSimulatorImpl extends AbstractConcurrentSimulator {

    public ConcurrentSimulatorImpl(int numBodies, int numSteps, int sideLenght, int nThreads) {
        super(numBodies, numSteps, sideLenght, nThreads);
    }

    @Override
    public void run() {
        long iteration = 0;
        while (iteration < super.getNumSteps()) {
            super.createLatch();
            super.createWorkers();
            for (Worker worker : super.getWorkers()) {
                worker.start();
            }
            try {
                super.getLatch().await();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            iteration++;
        }
    }
}
