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
        //long createTime = 0;
        //long startTime = 0;
        //long barrierTime = 0;
        while (iteration < super.getNumSteps()) {
            super.createLatch();
            //long start = System.currentTimeMillis();
            this.createWorkers(super.getWorkers().length);
            //long create = System.currentTimeMillis();
            for (Worker worker : super.getWorkers()) {
                worker.start();
            }
            //long compute = System.currentTimeMillis();
            try {
                super.getLatch().await();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            //long finale = System.currentTimeMillis();
            /*createTime += create - start;
            startTime += compute - create;
            barrierTime += finale - compute;*/
            iteration++;
        }
        /*System.out.println("Time for creation: " + createTime + "ms");
        System.out.println("Time for starting: " + startTime + "ms");
        System.out.println("Time for barrier: " + barrierTime + "ms");*/
    }
}
