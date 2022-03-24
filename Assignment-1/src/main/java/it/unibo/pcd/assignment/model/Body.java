package it.unibo.pcd.assignment.model;


import it.unibo.pcd.assignment.InfiniteForceException;

/*
 * This class represents a body
 *
 */
public class Body {

    private static final double REPULSIVE_CONST = 0.01;
    private static final double FRICTION_CONST = 1;

    private final Position2d pos;
    private final Velocity2d vel;
    private final double mass;
    private final int id;

    public Body(int id, Position2d pos, Velocity2d vel, double mass) {
        this.id = id;
        this.pos = pos;
        this.vel = vel;
        this.mass = mass;
    }

    /**
     * Check if two bodies are the same.
     * @param b the target body.
     * @return true if they're the same body, false otherwise.
     */
    public boolean equals(Object b) {
        return ((Body) b).id == id;
    }

    /**
     * Update the position, according to current velocity
     * @param dt time elapsed
     */
    public void updatePos(double dt) {
        pos.sum(new Velocity2d(vel).scalarMul(dt));
    }

    /**
     * Update the velocity, given the instant acceleration
     *
     * @param acc instant acceleration
     * @param dt  time elapsed
     */
    public void updateVelocity(Velocity2d acc, double dt) {
        vel.sum(new Velocity2d(acc).scalarMul(dt));
    }

    /**
     * Change the velocity
     * @param vx velocity on x-axis.
     * @param vy velocity on y-axis.
     */
    public void changeVel(double vx, double vy) {
        vel.change(vx, vy);
    }

    /**
     * Computes the distance from the specified body
     * @param b the target body.
     * @return the distance between bodies.
     */
    public double getDistanceFrom(Body b) {
        double dx = pos.getX() - b.getPos().getX();
        double dy = pos.getY() - b.getPos().getY();
        return Math.sqrt(dx * dx + dy * dy);
    }

    /**
     * Compute the repulsive force exerted by another body.
     * @param b the body.
     * @return the velocity of the repulsive force.
     * @throws InfiniteForceException if the force is infinite (Bodies are overlapping).
     */
    public Velocity2d computeRepulsiveForceBy(Body b) throws InfiniteForceException {
        double dist = getDistanceFrom(b);
        try {
            return new Velocity2d(b.getPos(), pos)
                    .normalize()
                    .scalarMul(b.getMass() * REPULSIVE_CONST / (dist * dist));
        } catch (Exception ex) {
            throw new InfiniteForceException();
        }
    }

    /**
     * Compute current friction force, given the current velocity.
     * @return The new velocity.
     */
    public Velocity2d getCurrentFrictionForce() {
        return new Velocity2d(vel).scalarMul(-FRICTION_CONST);
    }

    /**
     * Check if there are collisions with the boundary and update the
     * position and velocity accordingly.
     * @param bounds the field's bound.
     */
    public void checkAndSolveBoundaryCollision(final Boundary bounds) {
        double x = pos.getX();
        double y = pos.getY();

        if (x > bounds.getX1()) {
            pos.change(bounds.getX1(), pos.getY());
            vel.change(-vel.getX(), vel.getY());
        } else if (x < bounds.getX0()) {
            pos.change(bounds.getX0(), pos.getY());
            vel.change(-vel.getX(), vel.getY());
        }
        if (y > bounds.getY1()) {
            pos.change(pos.getX(), bounds.getY1());
            vel.change(vel.getX(), -vel.getY());
        } else if (y < bounds.getY0()) {
            pos.change(pos.getX(), bounds.getY0());
            vel.change(vel.getX(), -vel.getY());
        }
    }

    public double getMass() {
        return mass;
    }

    public Position2d getPos() {
        return pos;
    }
}
