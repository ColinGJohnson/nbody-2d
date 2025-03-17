package dev.cgj.nbody2d;

import dev.cgj.nbody2d.util.BoundedQueue;
import lombok.Getter;

import java.awt.Color;

/**
 * An object used to represent a body. Each body keeps track of its position, velocity, and the
 * forces acting on it.
 */
public class Body2d {

    /**
     * Softening parameter (epsilon) determining the minimum distance between this body and another
     * when calculating forces to avoid infinite forces at very short distances.
     */
    public static final double EPS = 3E4;

    /**
     * Universal gravitational constant.
     */
    public static final double G = 6.673e-11;

    public final Body2dState state = new Body2dState();

    @Getter
    private BoundedQueue<Body2dState> history = new BoundedQueue<>(100);

    /**
     * Body Constructor; bodies initially have 0 velocity relative to the origin and have their
     * color set to white.
     *
     * @param x the initial x-position of this body
     * @param y the initial y-position of this body
     * @param mass the mass of this body
     * @param r the radius of this body
     */
    public Body2d(double x, double y, double mass, double r) {
        this.state.setX(x);
        this.state.setY(y);
        this.state.setMass(mass);
        this.state.setR(r);
        this.state.setColor(Color.WHITE);
    }

    /**
     * Updates the color of this body to match the current force acting on it. The color is
     * assigned relative to a given limit. e.g. force 50% of the limit results in a color 50%
     * through the range of HSB hues.
     *
     * @param limit a reference value
     */
    public void updateColor(double limit) {
        double v = Math.sqrt(state.getFx() * state.getFx() + state.getFy() * state.getFy());
        float h = 0.5f * (float)Math.pow((v/limit), 0.3);
        float s = 0.7f;
        float b = 1f;
        state.setColor(Color.getHSBColor(h, s, b));
    }

    /**
     * Updates the forces currently acting on this body using Newtonian Gravity. Does not affect
     * position or velocity.
     *
     * @param environment an ArrayList of other bodies whose gravity should be considered.
     */
    public void updateForces(Body2d[] environment) {

        // reset forces before recalculating
        state.setFx(0);
        state.setFy(0);

        for (Body2d b : environment) {

            // don't calculate the force due to gravity between two bodies which are the same.
            if (b == this)  continue;

            // The two bodies cannot be so close that they would overlap.
            double minDistance = this.state.getR() + b.state.getR();
            double dist = Math.max(minDistance, distBetween(this.state.getX(), this.state.getY(), b.state.getX(), b.state.getY()));
            double F = (G * this.state.getMass() * b.state.getMass()) / (dist * dist + EPS * EPS);
            state.setFx(state.getFx() + F * ((b.state.getX() - this.state.getX()) / dist));
            state.setFy(state.getFy() + F * ((b.state.getY() - this.state.getY()) / dist));
        }
    }

    /**
     * Calculates the distance between two points.
     *
     * @param x1 the x-coordinate of the first point
     * @param y1 the y-coordinate of the first point
     * @param x2 the x-coordinate of the second point
     * @param y2 the y-coordinate of the second point
     * @return the distance (in meters) between the two points
     */
    public static double distBetween(double x1, double y1, double x2, double y2) {
        double dx = x2 - x1;
        double dy = y2 - y1;

        // Faster than Math.hypot(dx, dy), but with worse handling of overflow or underflow.
        return Math.sqrt(dx * dx + dy * dy);
    }

    /**
     * Calculates the distance between two bodies.
     *
     * @param a the first body
     * @param b the second body
     * @return the distance (in meters) between bodies a and b
     */
    public static double distBetween(Body2d a, Body2d b) {
        return distBetween(a.state.getX(), a.state.getY(), b.state.getX(), b.state.getY());
    }

    /**
     * Calculates the distance from a body to a given point.
     *
     * @param body the body
     * @param x the x position of the point
     * @param y the y position of the point
     * @return the distance (in meters) from the body to the point
     */
    public static double distFrom(Body2d body, double x, double y) {
        return distBetween(body.state.getX(), body.state.getY(), x, y);
    }

    /**
     * Calculates the distance from a body to the origin (0,0).
     *
     * @param body The body to calculate distance to.
     * @return the distance (in meters) from the body to the origin.
     */
    public static double distFromOrigin(Body2d body) {
        return distFrom(body, 0, 0);
    }

    /**
     * Updates the velocity of this body given the forces currently acting on it. Does not affect
     * position or forces.
     *
     * @param dt delta time; the length of time for which the acceleration produced by the current
     *           forces on this body should be applied
     */
    public void updateVelocity(double dt) {
        state.setVx(state.getVx() + dt * state.getFx() / state.getMass());
        state.setVy(state.getVy() + dt * state.getFy() / state.getMass());
    }

    /**
     * Updates the position of this body given the current velocity. Does not affect forces or
     * velocity.
     *
     * @param dt delta time
     */
    public void updatePosition(double dt) {
        state.setX(state.getX() + dt * state.getVx());
        state.setY(state.getY() + dt * state.getVy());
        history.add(new Body2dState(state));
    }
}
