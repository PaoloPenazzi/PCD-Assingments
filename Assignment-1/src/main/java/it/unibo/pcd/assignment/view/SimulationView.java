package it.unibo.pcd.assignment.view;

import it.unibo.pcd.assignment.model.Position2d;
import it.unibo.pcd.assignment.model.Body;
import it.unibo.pcd.assignment.model.Boundary;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.List;

/**
 * Simulation view
 *
 * @author aricci
 */
public class SimulationView {
    private final VisualiserFrame frame;

    /**
     * Creates a view of the specified size (in pixels).
     *
     * @param width  the panel width.
     * @param height the panel height.
     */
    public SimulationView(int width, int height) {
        frame = new VisualiserFrame(width, height);
    }

    public void display(List<Body> bodies, double vt, long iter, Boundary bounds) {
        frame.display(bodies, vt, iter, bounds);
    }

    public static class VisualiserFrame extends JFrame {
        private final VisualiserPanel panel;

        public VisualiserFrame(int width, int height) {
            setTitle("Bodies Simulation");
            setSize(width, height);
            setResizable(false);
            panel = new VisualiserPanel(width, height);
            getContentPane().add(panel);
            addWindowListener(new WindowAdapter() {
                public void windowClosing(WindowEvent ev) {
                    System.exit(-1);
                }
                public void windowClosed(WindowEvent ev) {
                    System.exit(-1);
                }
            });
            this.setVisible(true);
        }

        public void display(List<Body> bodies, double virtualTime, long iteration, Boundary bounds) {
            try {
                SwingUtilities.invokeAndWait(() -> {
                    panel.display(bodies, virtualTime, iteration, bounds);
                    repaint();
                });
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

        public void updateScale(double k) {
            panel.updateScale(k);
        }
    }

    public static class VisualiserPanel extends JPanel implements KeyListener {
        private static final int KEY_UP_CODE = 38;
        private static final int KEY_DOWN_CODE = 40;
        private List<Body> bodies;
        private Boundary bounds;
        private long iteration;
        private double virtualTime;
        private double scale = 1;
        private final long dx;
        private final long dy;

        public VisualiserPanel(int width, int height) {
            setSize(width, height);
            dx = width / 2 - 20;
            dy = height / 2 - 20;
            this.addKeyListener(this);
            setFocusable(true);
            setFocusTraversalKeysEnabled(false);
            requestFocusInWindow();
        }

        public void paint(Graphics g) {
            if (bodies != null) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                g2.clearRect(0, 0, this.getWidth(), this.getHeight());
                int x0 = getXCoordinate(bounds.getX0());
                int y0 = getYCoordinate(bounds.getY0());
                int wd = getXCoordinate(bounds.getX1()) - x0;
                int ht = y0 - getYCoordinate(bounds.getY1());
                g2.drawRect(x0, y0 - ht, wd, ht);
                bodies.forEach(b -> {
                    Position2d p = b.getPos();
                    int radius = (int) (10 * scale);
                    if (radius < 1) {
                        radius = 1;
                    }
                    g2.drawOval(getXCoordinate(p.getX()), getYCoordinate(p.getY()), radius, radius);
                });
                String time = String.format("%.2f", virtualTime);
                g2.drawString("Bodies: " + bodies.size() + " - vt: " + time + " - nIter: " + iteration + " (UP for zoom in, DOWN for zoom out)", 2, 20);
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

        @Override
        public void keyPressed(KeyEvent e) {
            if (e.getKeyCode() == KEY_UP_CODE) {        /* KEY UP */
                this.scale *= 1.1;
            } else if (e.getKeyCode() == KEY_DOWN_CODE) {    /* KEY DOWN */
                this.scale *= 0.9;
            }
        }

        public void keyReleased(KeyEvent e) {}

        public void keyTyped(KeyEvent e) {}
    }
}
