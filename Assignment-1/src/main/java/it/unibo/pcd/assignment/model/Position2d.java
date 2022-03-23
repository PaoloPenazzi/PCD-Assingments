package it.unibo.pcd.assignment.model;

public class Position2d {

    private double x, y;

    public Position2d(double x, double y){
        this.x = x;
        this.y = y;
    }

    public Position2d sum(Velocity2d v) {
    	x += v.x;
    	y += v.y;
    	return this;
    }
     
    public void change(double x, double y){
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
