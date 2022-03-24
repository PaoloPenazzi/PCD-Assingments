package it.unibo.pcd.assignment.model;

public class Position2d {
    private double x;
    private double y;

    public Position2d(double x, double y){
        this.x = x;
        this.y = y;
    }

    public void sum(Velocity2d v) {
    	x += v.x;
    	y += v.y;
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
