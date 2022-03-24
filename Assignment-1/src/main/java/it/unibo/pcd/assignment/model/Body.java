package it.unibo.pcd.assignment.model;


import it.unibo.pcd.assignment.InfiniteForceException;

/*
 * This class represents a body
 *
 */
public class Body {
    private static final double REPULSIVE_CONST = 0.01;
    private static final double FRICTION_CONST = 1;

    private final Position2d position;
    private final Velocity2d velocity;
    private final double mass;
    private final int id;

    public Body(int id, Position2d position, Velocity2d velocity, double mass) {
        this.id = id;
        this.position = position;
        this.velocity = velocity;
        this.mass = mass;
    }

    /**
     * Check if two bodies are the same.
     * @param body the target body.
     * @return true if they're the same body, false otherwise.
     */
    public boolean equals(Object body) {
        return ((Body) body).id == id;
    }

    /**
     * Update the position, according to current velocity
     * @param deltaTime time elapsed
     */
    public void updatePos(double deltaTime) {
        position.sum(new Velocity2d(velocity).scalarMul(deltaTime));
    }

    /**
     * Update the velocity, given the instant acceleration
     *
     * @param acceleration instant acceleration
     * @param deltaTime  time elapsed
     */
    public void updateVelocity(Velocity2d acceleration, double deltaTime) {
        velocity.sum(new Velocity2d(acceleration).scalarMul(deltaTime));
    }

    /**
     * Change the velocity
     * @param xVelocity velocity on x-axis.
     * @param yVelocity velocity on y-axis.
     */
    public void changeVel(double xVelocity, double yVelocity) {
        velocity.change(xVelocity, yVelocity);
    }

    /**
     * Computes the distance from the specified body
     * @param body the target body.
     * @return the distance between bodies.
     */
    public double getDistanceFrom(Body body) {
        double dx = position.getX() - body.getPosition().getX();
        double dy = position.getY() - body.getPosition().getY();
        return Math.sqrt(dx * dx + dy * dy);
    }

    /**
     * Compute the repulsive force exerted by another body.
     * @param body the body.
     * @return the velocity of the repulsive force.
     * @throws InfiniteForceException if the force is infinite (Bodies are overlapping).
     */
    public Velocity2d computeRepulsiveForceBy(Body body) throws InfiniteForceException {
        double dist = getDistanceFrom(body);
        try {
            return new Velocity2d(body.getPosition(), position)
                    .normalize()
                    .scalarMul(body.getMass() * REPULSIVE_CONST / (dist * dist));
        } catch (Exception ex) {
            throw new InfiniteForceException();
        }
    }

    /**
     * Compute current friction force, given the current velocity.
     * @return The new velocity.
     */
    public Velocity2d getCurrentFrictionForce() {
        return new Velocity2d(velocity).scalarMul(-FRICTION_CONST);
    }

    /**
     * Check if there are collisions with the boundary and update the
     * position and velocity accordingly.
     * @param bounds the field's bound.
     */
    public void checkAndSolveBoundaryCollision(final Boundary bounds) {
        double x = position.getX();
        double y = position.getY();

        if (x > bounds.getX1()) {
            position.change(bounds.getX1(), position.getY());
            velocity.change(-velocity.getX(), velocity.getY());
        } else if (x < bounds.getX0()) {
            position.change(bounds.getX0(), position.getY());
            velocity.change(-velocity.getX(), velocity.getY());
        }
        if (y > bounds.getY1()) {
            position.change(position.getX(), bounds.getY1());
            velocity.change(velocity.getX(), -velocity.getY());
        } else if (y < bounds.getY0()) {
            position.change(position.getX(), bounds.getY0());
            velocity.change(velocity.getX(), -velocity.getY());
        }
    }

    public double getMass() {
        return mass;
    }

    public Position2d getPosition() {
        return position;
    }
}
