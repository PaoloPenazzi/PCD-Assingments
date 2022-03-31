package it.unibo.pcd.assignment.controller;

import it.unibo.pcd.assignment.model.Monitor;
import it.unibo.pcd.assignment.model.Worker;

import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.CyclicBarrier;

public class ConcurrentSimulatorImplWithGUI extends AbstractConcurrentSimulator {
    private final ViewController view;
    private double virtualTime;
    private int iteration;
    private final Monitor pause;

    public ConcurrentSimulatorImplWithGUI(int numBodies, int numSteps, int sideLenght, int numThread) {
        super(numBodies, numSteps, sideLenght, numThread);
        this.pause = new Monitor();
        this.view = new ViewController(620, 620, this.pause);
        this.virtualTime = 0;
        this.iteration = 0;
    }

    @Override
    public void run() {
        while (this.iteration < super.getNumSteps()) {
            synchronized (this) {
                while (this.pause.isPaused()) {

                }
            }
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
