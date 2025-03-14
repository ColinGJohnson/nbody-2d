package dev.cgj.nbody2d;

import lombok.Getter;
import lombok.Setter;

import java.util.TimerTask;

/**
 * Brute-force 2-dimensional Newtonian Gravity n-body simulation.
 */
public class NBody2d {

    public static final double MOON_MASS = 7.347e10;   // moon mass (kilograms)
    public static final double EARTH_MASS = 5.972e24;  // mass of the earth (kilograms)
    public static final double EARTH_RADIUS = 6.356e6; // radius of the earth (meters)
    public static final double SUN_MASS = 1.989e30;    // mass of the sun (kilograms)
    public static final double SUN_RADIUS = 6.955e8;   // radius of the sun
    public static final double MARS_DIST = 2.3816e11;  // distance to Mars (meters)

    /**
     * The number of bodies being simulated
     */
    @Getter
    private int n;

    /**
     * An array of {@link Body2d} objects representing the bodies participating in the simulation.
     * Each body tracks its position, velocity, and the forces acting upon it. These objects
     * are used to model the gravitational interactions between the bodies in the N-body simulation.
     */
    @Getter
    private final Body2d[] bodies;

    /**
     * The edge of this simulation's universe.
     */
    @Getter
    private final double boundary;

    /**
     * The amount of simulated time that has passed so far (seconds).
     */
    @Setter
    @Getter
    private long timeElapsed;

    /**
     * The amount of simulated time between steps (seconds).
     */
    private final double dt;

    /**
     * True when the simulation is automatically calling step().
     */
    boolean  autoStep;

    /**
     * Timer for autoStep().
     */
    java.util.Timer timer;

    /**
     * NBody2d Constructor.
     *
     * @param boundary the boundary of the simulation.
     * @param dt delta time (seconds)
     * @param n the number of bodies to simulate
     * @param mass the mass for the simulated bodies
     */
    public NBody2d(double boundary, double dt, int n, double mass, double radius) {
        this.boundary = boundary;
        this.dt = dt;
        this.n = n;
        bodies = new Body2d[n];

        // populate the array with the specified number of bodies
        for (int i = 0; i < n; i ++) {
            bodies[i] = new Body2d(0, 0, mass, radius);
            bodies[i].state.setVx(10000);
            bodies[i].state.setVy(10000);
        }

        randomizePositions(boundary / 2);

        bodies[0].state.setMass(SUN_MASS);
        bodies[0].state.setR(SUN_RADIUS);
        bodies[0].state.setX(0);
        bodies[0].state.setY(0);
        bodies[0].state.setVx(0);
        bodies[0].state.setVy(0);
    }

    /**
     * Constructs an NBody2d simulation using a preexisting set of bodies.
     *
     * @param boundary the boundary of the simulation.
     * @param dt delta time (seconds)
     * @param bodies an array of bodies to run the simulation
     */
    public NBody2d(double boundary, double dt, Body2d[] bodies) {
        this.boundary = boundary;
        this.dt = dt;
        this.bodies = bodies;
    }

    /**
     * Relocates this simulation's bodies to 'n' random locations with both coordinates within
     * 'boundary' meters of the origin.
     */
    public void randomizePositions(double limit) {
        for (Body2d body : bodies) {

            // pick a random angle in [0, 2pi) and a random distance in [0, boundary)
            double angle = Math.random() * (2 * Math.PI);
            double distance = Math.pow(Math.random(), 0.5) * limit;

            // calculate (x,y) coordinate of this point and assign to current body
            body.state.setX(Math.cos(angle) * distance);
            body.state.setY(Math.sin(angle) * distance);
        }
    }

    /**
     * Get the maximum force acting on any {@link Body2d} in the simulation.
     *
     * @return The maximum force in Newtons.
     */
    public double getMaxForce() {
        double maxForce = 0;
        for (Body2d body : bodies) {
            double currentForce = Math.sqrt(body.state.getFx() * body.state.getFx() + body.state.getFy() * body.state.getFy());
            if (currentForce > maxForce) maxForce = currentForce;
        }
        return maxForce;
    }

    /**
     * Advances the simulation by one time step.
     */
    public void step() {

        // update the forces acting on each body
        for (Body2d body : bodies) {
            body.updateForces(bodies);
        }

        // update the positions and colors of each body
        for (Body2d body : bodies) {
            body.updateVelocity(dt);
            body.updateColor(getMaxForce());
            body.updatePosition(dt);
        }

        // record the time elapsed
        timeElapsed += (long) dt;
    }

    /**
     * Automatically step the simulation at a real-world time interval. Call stopAutoStep to stop
     * this behavior.
     *
     * @param stepDelay the amount of time to wait between simulation steps.
     */
    public void autoStep(long stepDelay) {
        timer = new java.util.Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                step();
            }
        }, 0, stepDelay);
        autoStep = true;
    }

    /**
     * Stops the autoStep timer. If autoStep is not running, this method does nothing.
     */
    public void stopAutoStep() {
        timer.cancel();
        autoStep = false;
    }
}
