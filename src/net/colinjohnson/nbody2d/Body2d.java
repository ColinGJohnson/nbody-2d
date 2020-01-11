package net.colinjohnson.nbody2d;

import java.awt.*;

/**
 * An object used to represent a body. Each body keeps track of its position, velocity, and the
 * forces acting on it.
 *
 * @Author Colin Johnson
 */
public class Body2d {

    // universal gravitational constant
    private static final double G = 6.673e-11;

    public double x;     // x-distance from the origin (in meters)
    public double y;     // y-distance from the origin (in meters)
    public double vx;    // x-velocity (in meters per second)
    public double vy;    // y-velocity (in meters per second)
    public double fx;    // x-force (in Newtons)
    public double fy;    // y-force (in Newtons)
    public double mass;  // the mass of this body (in kilograms)
    public Color color;  // the color of this body (not used in calculations)

    /**
     * Body Constructor; bodies initially have 0 velocity relative to the origin and have their
     * color set to white.
     *
     * @param x the initial x-position of this body
     * @param y the initial y-position of this body
     * @param mass the mass of this body
     */
    public Body2d(double x, double y, double mass) {
        this.x = x;
        this.y = y;
        this.mass = mass;
        this.color = Color.WHITE;
    }

    /**
     * Updates the color of this body to match it's current velocity. The color is assigned relative
     * to a given limit. e.g. velocity 50% of the limit results in a color 50% through the range
     * of HSB hues.
     *
     * @param limit a reference value
     */
    private void updateColor(double limit) {
        double v = Math.sqrt(vx * vx + vy * vy);
        float h = (float) (1f - (v/limit));
        float s = 0.5f;;
        float b = 1f;
        color = Color.getHSBColor(h, s, b);
    }

    /**
     * Updates the forces currently acting on this body. *Does not effect position or velocity*.
     *
     * @param environment an ArrayList of other bodies whose gravity should be considered.
     */
    public void updateForces(Body2d[] environment) {
        for (Body2d body : environment) {

            // don't calculate the force due to gravity between two bodies which are the same.
            if (body == this) {
                continue;
            }

            // Force due to gravity = Gm1m2/r^2

            // x component of force

            // y component of force
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
        return Math.sqrt(Math.pow(x1 - x2, 2) + Math.pow(y1 - y2, 2));
    }

    /**
     * Calculates the distance between two bodies.
     *
     * @param a the first body
     * @param b the second body
     * @return the distance (in meters) between bodies a and b
     */
    public static double distBetween(Body2d a, Body2d b) {
        return distBetween(a.x, a.y, b.x, b.y);
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
        return distBetween(body.x, body.y, x, y);
    }

    /**
     * Calculates the distance from a body to the origin (0,0)
     *
     * @param body
     * @return the distance (in meters) from the body to the origin
     */
    public static double distFromOrigin(Body2d body) {
        return distFrom(body, 0, 0);
    }

    /**
     * Updates the velocity of this body given the forces currently acting on it *does not effect
     * position or forces*.
     *
     * @param dt delta time; the length of time for which the acceleration produced by the current
     *           forces on this body should be applied
     */
    public void updateVelocity(double dt) {

    }

    /**
     * Updates the position of this body given the current velocity *does not effect forces or
     * velocity*
     *
     * @param dt delta time
     */
    public void updatePosition(double dt) {

        // make sure bodies do not move past the boundary of the simulation
    }
}
