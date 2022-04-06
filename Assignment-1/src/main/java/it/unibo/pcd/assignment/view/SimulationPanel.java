package it.unibo.pcd.assignment.view;

import it.unibo.pcd.assignment.model.Body;
import it.unibo.pcd.assignment.model.Boundary;
import it.unibo.pcd.assignment.model.Position2d;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.util.List;

public class SimulationPanel extends JPanel {
    private List<Body> bodies;
    private Boundary bounds;
    private long iteration;
    private double virtualTime;
    private double scale = 1;
    private final long dx;
    private final long dy;


    public SimulationPanel(int width, int height) {
        setSize(width, height);
        dx = width / 2 - 20;
        dy = height / 2 - 20;
    }

    public void paint(Graphics g) {
        if (bodies != null) {
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
            g2.clearRect(0, this.getHeight(), this.getWidth(), this.getHeight());
            int x0 = getXCoordinate(bounds.getX0());
            int y0 = getYCoordinate(bounds.getY0());
            int wd = getXCoordinate(bounds.getX1()) - x0;
            int ht = y0 - getYCoordinate(bounds.getY1());
            g2.drawRect(x0, y0 - ht, wd, ht);
            bodies.forEach(b -> {
                Position2d p = b.getPosition();
                int radius = (int) (10 * scale);
                if (radius < 1) {
                    radius = 1;
                }
                g2.drawOval(getXCoordinate(p.getX()), getYCoordinate(p.getY()), radius, radius);
            });
            String time = String.format("%.2f", virtualTime);
            // g2.drawString("Bodies: " + bodies.size() + " - vt: " + time + " - nIter: " +
            // iteration + " (+ for zoom in, - for zoom out)", 2, (int)(this.getHeight() * 0.1) + 20);
            g2.drawString("Bodies: " + bodies.size() + " - virtualTime: " + time + " - iteration: " +
                    iteration + " (+ for zoom in, - for zoom out)", 2, 45);
        }
    }

    private int getXCoordinate(double x) {
        return (int) (dx + x * dx * scale);
    }

    private int getYCoordinate(double y) {
        return (int) (dy - y * dy * scale);
    }

    public void display(List<Body> bodies, double virtualTime, long iteration, Boundary bounds) {
        this.bodies = bodies;
        this.bounds = bounds;
        this.virtualTime = virtualTime;
        this.iteration = iteration;
    }

    public void updateScale(double k) {
        this.scale *= k;
    }
}

