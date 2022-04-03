package it.unibo.pcd.assignment;

import it.unibo.pcd.assignment.controller.ConcurrentSimulatorImpl;
import org.junit.Before;
import org.junit.Test;
import org.junit.Assert;

public class testConcurrentSimulator {
    private ConcurrentSimulatorImpl concurrentSimulator;

    @Before
    public void before() {
        concurrentSimulator = new ConcurrentSimulatorImpl(10, 10, 10, 5);
    }

    @Test
    public void testCreateField() {
        Assert.assertEquals(-10.0, this.concurrentSimulator.getBounds().getX0(), 0.001);
        Assert.assertEquals(10.0, this.concurrentSimulator.getBounds().getX1(), 0.001);
        Assert.assertEquals(-10.0, this.concurrentSimulator.getBounds().getY0(), 0.001);
        Assert.assertEquals(10.0, this.concurrentSimulator.getBounds().getY1(), 0.001);
    }

    @Test
    public void testSpawnBodies() {
        Assert.assertEquals(10, this.concurrentSimulator.getBodies().size());
    }

    @Test
    public void testCreateWorkers() {
        Assert.assertEquals(5, this.concurrentSimulator.getWorkers().length);
    }

    @Test
    public void testCreateLatch() {
        this.concurrentSimulator.createLatch();
        Assert.assertEquals(5, this.concurrentSimulator.getLatch().getCount());
    }

}
