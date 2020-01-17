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
    public double r;     // physical radius of this body (in meters)
    public double mass;  // the mass of this body (in kilograms)
    public Color color;  // the color of this body (not used in calculations)

    /**
     * Body Constructor; bodies initially have 0 velocity relative to the origin and have their
     * color set to white.
     *
     * @param x the initial x-position of this body
     * @param y the initial y-position of this body
     * @param mass the mass of this body
     * @param r the radius of this body
     */
    public Body2d(double x, double y, double mass, double  r) {
        this.x = x;
        this.y = y;
        this.mass = mass;
        this.r = r;
        this.color = Color.WHITE;
    }

    /**
     * Updates the color of this body to match the current force acting on it. The color is
     * assigned relative to a given limit. e.g. force 50% of the limit results in a color 50%
     * through the range of HSB hues.
     *
     * @param limit a reference value
     */
    public void updateColor(double limit) {
        double v = Math.sqrt(fx * fx + fy * fy);
        float h = 0.5f * (float)Math.pow((v/limit), 0.3);
        float s = 0.7f;;
        float b = 1f;
        color = Color.getHSBColor(h, s, b);
    }

    /**
     * Updates the forces currently acting on this body using Newtonian Gravity. *Does not effect
     * position or velocity*.
     *
     * @param environment an ArrayList of other bodies whose gravity should be considered.
     */
    public void updateForces(Body2d[] environment) {

        //TODO: use multiple cores?
        //int cores = Runtime.getRuntime().availableProcessors();
        //System.out.println("Starting simulation using " + cores + " cores.");

        // reset forces before recalculating
        fx = 0;
        fy = 0;

        for (Body2d b : environment) {

            // don't calculate the force due to gravity between two bodies which are the same.
            if (b == this)  continue;

            // TODO: switch from EPS softening parameter to physical size for bodies
            // TODO: switch to using trig.
            double EPS = 3E4;
            double dist = distBetween(this.x, this.y, b.x, b.y);
            double F = (G * this.mass * b.mass) / (dist * dist + EPS*EPS);
            fx += F * ((b.x - this.x) / dist);
            fy += F * ((b.y - this.y) / dist);
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
        vx += dt * fx / mass;
        vy += dt * fy / mass;
    }

    /**
     * Updates the position of this body given the current velocity *does not effect forces or
     * velocity*
     *
     * @param dt delta time
     */
    public void updatePosition(double dt) {
        x += dt * vx;
        y += dt * vy;
    }
}
