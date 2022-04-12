package it.unibo.pcd.assignment.task.controller;

import it.unibo.pcd.assignment.task.model.TaskPosition;
import it.unibo.pcd.assignment.task.model.TaskVelocity;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.*;

public class Master extends AbstractSimulator {
    private final ExecutorService executor;
    private final int taskNumber;
    private TaskVelocity[] taskVelocityArray;
    private TaskPosition[] taskPositionArray;
    private final List<Future<Void>> futureList;

    public Master(int numBodies, int numSteps, int sideLenght, int threadNumber, int taskNumber) {
        super(numBodies, numSteps, sideLenght);
        this.futureList = new ArrayList<>();
        this.taskNumber = taskNumber;
        this.executor = Executors.newFixedThreadPool(threadNumber);
    }

    @Override
    public void run() {
        int iteration = 0;
        while (iteration < super.getNumSteps()) {
            this.createTaskVelocity();
            for (TaskVelocity taskVelocity : taskVelocityArray) {
                futureList.add(this.executor.submit(taskVelocity));
            }
            this.waitForFuture();
            futureList.clear();
            this.createTaskPosition();
            for (TaskPosition taskPosition : taskPositionArray) {
                futureList.add(this.executor.submit(taskPosition));
            }
            this.waitForFuture();
            iteration++;
        }
    }

    private void waitForFuture() {
        for (Future<Void> future : futureList) {
            try {
                future.get();
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        }
    }

    private void createTaskPosition() {
        this.taskPositionArray = new TaskPosition[this.taskNumber];
        int bodiesPerTask = super.getBodies().size() / this.taskNumber;
        for (int i = 0; i < this.taskNumber; i++) {
            if (i == this.taskNumber - 1) {
                taskPositionArray[i] = new TaskPosition(i * bodiesPerTask, super.getBodies().size(),
                        super.getBodies());
            } else {
                taskPositionArray[i] = new TaskPosition(i * bodiesPerTask, ((i + 1) * bodiesPerTask),
                        super.getBodies());
            }
        }
    }

    private void createTaskVelocity() {
        this.taskVelocityArray = new TaskVelocity[this.taskNumber];
        int bodiesPerTask = super.getBodies().size() / this.taskNumber;
        for (int i = 0; i < this.taskNumber; i++) {
            if (i == this.taskNumber - 1) {
                taskVelocityArray[i] = new TaskVelocity(i * bodiesPerTask, super.getBodies().size(),
                        super.getBodies());
            } else {
                taskVelocityArray[i] = new TaskVelocity(i * bodiesPerTask, ((i + 1) * bodiesPerTask),
                        super.getBodies());
            }
        }
    }
}
