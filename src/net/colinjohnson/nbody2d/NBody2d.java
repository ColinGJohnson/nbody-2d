package net.colinjohnson.nbody2d;

/**
 * A simple brute-force 2-dimensional n-body simulation using Newtonian Gravity.
 *
 * @Author Colin Johnson
 */
public class NBody2d {

    // useful constants
    public static final double MOON_MASS = 7.347e10;   // moon mass (kilograms)
    public static final double EARTH_MASS = 5.972e24;  // mass of the earth (kilograms)
    public static final double SUN_MASS = 1.989e30;    // mass of the sun (kilograms)
    public static final double MARS_DIST = 2.3816e11;  // distance to mars (meters)

    // simulation parameters
    private int n;              // the number of bodies being simulated
    private Body2d[] bodies;    // Array of bodies
    private double boundary;    // the edge of this simulation's "universe"
    private double dt;          // the amount of simulated time between steps (seconds)

    /**
     * Main method which creates and configures an nbody2d.NBody2d simulation.
     * @param args if an integer is passed as an argument then it will be used as n
     */
    public static void main(String[] args) {

        //TODO: use multiple cores?
        //int cores = Runtime.getRuntime().availableProcessors();
        //System.out.println("Starting simulation using " + cores + " cores.");

        // get 'n' from command line parameter, if one exists, otherwise use default value
        int n = 100;
        if (args.length > 0) {
            try {
                n = Integer.parseInt(args[0]);
            } catch (NumberFormatException e) {
                System.err.println("Error parsing 'n' from command line parameter.");
            }
        }
        System.out.format("Starting simulation with n=%d bodies\n", n);

        // create and configure the simulation
        // NBody2d sim = new NBody2d(MARS_DIST, 86400, n, EARTH_MASS);
        NBody2d sim = new NBody2d(400, 1000, 5000, 10);

        // create a window to view the current state of the simulation
        NBody2dViewer viewer = new NBody2dViewer(sim);

        // run the simulation
        while (true) {

            // advance the simulation by one time step
            sim.step();

            // update the display if its loaded
            if (viewer.isReady()) {
                viewer.update();
            }

            // wait if we finished too fast
            try {
                Thread.sleep(1000/60);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * nbody2d.NBody2d Constructor.
     *
     * @param boundary the boundary of the simulation.
     * @param dt delta time (seconds)
     * @param n the number of bodies to simulate
     * @param mass the mass for the simulated bodies
     */
    public NBody2d(double boundary, double dt, int n, double mass) {
        this.boundary = boundary;
        this.dt = dt;
        this.n = n;
        bodies = new Body2d[n];

        // populate the array with the specified number of bodies
        for (int i = 0; i < n; i ++) {
            bodies[i] = new Body2d(0, 0, mass);
        }
        randomizePositions();
    }

    /**
     * Constructs an nbody2d.NBody2d simulation using a preexisting set of bodies.
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
     * Relocates bodies to 'n' random locations with both coordinates within 'boundary' meters of
     * the origin.
     */
    private void randomizePositions() {
        for (Body2d body : bodies) {

            // pick a random angle in [0, 2pi) and a random distance in [0, boundary)
            double angle = Math.random() * (2 * Math.PI);
            double distance = Math.pow(Math.random(), 0.5) * boundary;

            // calculate (x,y) coordinate of this point and assign to current body
            body.x = Math.cos(angle) * distance;
            body.y = Math.sin(angle) * distance;
        }
    }

    /**
     * Advances the simulation by one time step.
     */
    public void step() {

        // update the forces acting on each body
        for (Body2d body : bodies) {
            body.updateForces(bodies);
        }

        // update the position of each body
        for (Body2d body : bodies) {
            body.updateVelocity(dt);
            body.updatePosition(dt);
        }
    }

    /**
     * Get the number of bodies tracked in this simulation.
     * @return the number of bodies
     */
    public int getN() {
        return n;
    }

    /**
     * Get the boundary of this simulation. No bodies are allows to exist beyong this distance
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
}
