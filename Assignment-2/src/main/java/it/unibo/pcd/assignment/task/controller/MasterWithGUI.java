package it.unibo.pcd.assignment.task.controller;

import it.unibo.pcd.assignment.task.model.Monitor;
import it.unibo.pcd.assignment.task.model.TaskPosition;
import it.unibo.pcd.assignment.task.model.TaskVelocity;

public class MasterWithGUI extends Master {
    private final ViewController view;
    private final Monitor monitor;
    private double virtualTime;

    public MasterWithGUI(int numBodies, int numSteps, int sideLenght, int taskNumber) {
        super(numBodies, numSteps, sideLenght, taskNumber);
        this.monitor = new Monitor();
        this.view = new ViewController(620, 620, this.monitor);
        this.virtualTime = 0;
    }

    @Override
    public void run() {
        int iteration = 0;
        while (iteration < super.getNumSteps()) {
            this.monitor.waitPauseTrue();
            this.createTaskVelocity();
            for (TaskVelocity taskVelocity : super.getTaskVelocityArray()) {
                super.getFutureList().add(super.getExecutor().submit(taskVelocity));
            }
            this.waitForFuture();
            this.createTaskPosition();
            for (TaskPosition taskPosition : super.getTaskPositionArray()) {
                super.getFutureList().add(super.getExecutor().submit(taskPosition));
            }
            this.waitForFuture();
            this.virtualTime = this.virtualTime + DELTA_TIME;
            iteration++;
            view.display(super.getBodies(), this.virtualTime, iteration, super.getBounds());
        }
        super.getExecutor().shutdown();
    }
}
