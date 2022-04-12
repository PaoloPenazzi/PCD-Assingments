/*
 * V2d.java
 *
 * Copyright 2000-2001-2002  aliCE team at deis.unibo.it
 *
 * This software is the proprietary information of deis.unibo.it
 * Use is subject to license terms.
 */
package it.unibo.pcd.assignment.task.model;

import it.unibo.pcd.assignment.task.NullVectorException;

/**
 * 2-dimensional vector
 * objects are completely state-less
 */
public class Velocity2d {
    public double x;
    public double y;

    public Velocity2d(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public Velocity2d(Velocity2d v) {
        this.x = v.x;
        this.y = v.y;
    }

    public Velocity2d(Position2d from, Position2d to) {
        this.x = to.getX() - from.getX();
        this.y = to.getY() - from.getY();
    }

    public Velocity2d scalarMul(double k) {
        x *= k;
        y *= k;
        return this;
    }

    public void sum(Velocity2d v) {
        x += v.x;
        y += v.y;
    }

    public Velocity2d normalize() throws NullVectorException {
        double mod = Math.sqrt(x * x + y * y);
        if (mod > 0) {
            x /= mod;
            y /= mod;
            return this;
        } else {
            throw new NullVectorException();
        }
    }

    public void change(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }
}
