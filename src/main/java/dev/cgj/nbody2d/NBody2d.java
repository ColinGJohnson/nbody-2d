package dev.cgj.nbody2d;

import java.util.TimerTask;

/**
 * A simple brute-force 2-dimensional n-body simulation using Newtonian Gravity.
 *
 * @author Colin Johnson
 */
public class NBody2d {

    // useful constants
    public static final double MOON_MASS = 7.347e10;   // moon mass (kilograms)
    public static final double EARTH_MASS = 5.972e24;  // mass of the earth (kilograms)
    public static final double EARTH_RADIUS = 6.356e6; // radius of the earth (meters)
    public static final double SUN_MASS = 1.989e30;    // mass of the sun (kilograms)
    public static final double SUN_RADIUS = 6.955e8;   // radius of the sun
    public static final double MARS_DIST = 2.3816e11;  // distance to Mars (meters)

    // simulation parameters
    private int n;                    // the number of bodies being simulated
    private final Body2d[] bodies;    // Array of bodies
    private final double boundary;    // the edge of this simulation's "universe"

    // simulation timing
    private final double dt;    // the amount of simulated time between steps (seconds)
    boolean  autoStep;          // true when the simulation is automatically calling step();
    java.util.Timer timer;      // timer for autoStep()

    // simulation trackers
    private long timeElapsed;   // the amount of simulated time that has passed so far (seconds)

    /**
     * Main method which creates and configures a {@link NBody2d} simulation.
     *
     * @param args if an integer is passed as an argument then it will determine the 'n' parameter.
     */
    public static void main(String[] args) {

        // get 'n' from command line parameter, if one exists, otherwise use default value
        int n = 500;
        if (args.length > 0) {
            try {
                n = Integer.parseInt(args[0]);
            } catch (NumberFormatException e) {
                System.err.println("Error parsing 'n' from command line parameter.");
            }
        }
        System.out.format("Starting simulation with n=%d bodies\n", n);

        // create and configure the simulation
        NBody2d sim = new NBody2d(MARS_DIST, 3600, n, EARTH_MASS, EARTH_RADIUS);

        // create a window to view the current state of the simulation
        NBody2dViewer viewer = new NBody2dViewer(sim);

        // start the simulation
        sim.autoStep(1000 / 60);

        // regularly update the viewer
        new java.util.Timer().scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                if (viewer.isReady()) {
                    viewer.update();
                }
            }
        }, 0, 1000 / 60);
    }

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
            bodies[i].vx = 10000;
            bodies[i].vy = 10000;
        }

        randomizePositions(boundary / 2);

        // TODO: remove after testing
        // add a sun
        bodies[0].mass = SUN_MASS;
        bodies[0].r = SUN_RADIUS;
        bodies[0].x = bodies[0].y = 0;
        bodies[0].vx = bodies[0].vy = 0;
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
            body.x = Math.cos(angle) * distance;
            body.y = Math.sin(angle) * distance;
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
            double currentForce = Math.sqrt(body.fx * body.fx + body.fy * body.fy);
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

        // update the positions of each body
        for (Body2d body : bodies) {
            body.updateVelocity(dt);
            body.updatePosition(dt);

            //TODO: reposition bodies who moved beyond the boundary
        }

        // update the colors of each body
        for (Body2d body : bodies) {
            body.updateColor(getMaxForce());
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

    /**
     * Get the number of bodies tracked in this simulation.
     * @return the number of bodies
     */
    public int getN() {
        return n;
    }

    /**
     * Get the boundary of this simulation. No bodies are allows to exist beyond this distance
     * from the origin.
     *
     * @return the distance, in meters, from the origin to the simulation's boundary
     */
    public double getBoundary() {
        return boundary;
    }

    public Body2d[] getBodies() {
        return bodies;
    }

    public long getTimeElapsed() {
        return timeElapsed;
    }

    public void setTimeElapsed(long timeElapsed) {
        this.timeElapsed = timeElapsed;
    }
}
