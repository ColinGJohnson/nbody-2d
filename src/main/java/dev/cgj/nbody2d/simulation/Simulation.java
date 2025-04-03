package dev.cgj.nbody2d.simulation;

import dev.cgj.nbody2d.config.InitialBodyConfig;
import dev.cgj.nbody2d.config.SimulationConfig;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.TimerTask;

/**
 * Brute-force 2-dimensional Newtonian Gravity n-body simulation.
 */
@Slf4j
public class Simulation {

    @Getter
    private final SimulationConfig config;

    @Getter
    private long stepTime;

    /**
     * An list of {@link Body} objects representing the bodies participating in the simulation.
     * Each body tracks its position, velocity, and the forces acting upon it. These objects
     * are used to model the gravitational interactions between the bodies in the N-body simulation.
     */
    @Getter
    private List<Body> bodies;

    /**
     * The amount of simulated time that has passed so far (seconds).
     */
    @Setter
    @Getter
    private long timeElapsed;

    /**
     * True when the simulation is automatically calling step().
     */
    @Getter
    boolean running;

    /**
     * Timer for autoStep().
     */
    java.util.Timer timer;

    /**
     * NBody2d Constructor.
     *
     * @param config Simulation configuration including boundary, deta time, and initial bodies.
     */
    public Simulation(SimulationConfig config) {
        this.config = config;
        resetBodies();
    }

    public void resetBodies() {
        int n = config.getInitialState().stream()
                .mapToInt(InitialBodyConfig::getN)
                .sum();
        bodies = new ArrayList<>(n);

        for (InitialBodyConfig init : config.getInitialState()) {
            for (int j = 0; j < init.getN(); j++) {
                Body body = new Body(init.getX(), init.getY(), init.getMass(), init.getR(), init.getVx(), init.getVy());
                randomizePosition(body, init.getPositionJitter());
                bodies.add(body);
            }
        }
    }

    /**
     * Moves the given body in a random direction within 'limit' meters of its original position.
     */
    public void randomizePosition(Body body, double limit) {
        if (limit < 0) {
            throw new IllegalArgumentException("limit must be greater than or equal to 0");
        }

        if (limit == 0) {
            return;
        }

        // pick a random angle in [0, 2pi) and a random distance in [0, boundary)
        double angle = Math.random() * (2 * Math.PI);
        double distance = Math.pow(Math.random(), 0.5) * limit;

        // calculate (x,y) coordinate of this point and assign to current body
        body.state = body.state.withX(Math.cos(angle) * distance);
        body.state = body.state.withY(Math.sin(angle) * distance);
    }

    /**
     * Get the maximum force acting on any {@link Body} in the simulation.
     *
     * @return The maximum force in Newtons.
     */
    public double getMaxForce() {
        double maxForce = 0;
        for (Body body : bodies) {
            double currentForce = Math.sqrt(body.state.getFx() * body.state.getFx() + body.state.getFy() * body.state.getFy());
            if (currentForce > maxForce) maxForce = currentForce;
        }
        return maxForce;
    }

    /**
     * Advances the simulation by one time step.
     */
    public void step() {
        long startTime = System.nanoTime();
        List<Body> active = bodies.stream().filter(Body::isActive).toList();

        // update the forces acting on each body
        for (Body body : active) {
            body.updateForces(active);
        }

        // update the positions and colors of each body
        for (Body body : active) {
            body.updateVelocity(config.getDt());
            body.updateColor(getMaxForce());
            body.updatePosition(config.getDt());
            body.applyBoundary(config.getBoundaryType(), config.getBoundary());
        }

        // record the time elapsed
        timeElapsed += (long) config.getDt();

        // Smooth measurement by averaging with previous
        stepTime = (stepTime + (System.nanoTime() - startTime)) / 2;
    }

    /**
     * Automatically step the simulation at a real-world time interval. Call stopAutoStep to stop
     * this behavior.
     *
     * @param stepDelay the amount of time to wait between simulation steps.
     */
    public void autoStep(long stepDelay) {
        stopAutoStep();
        timer = new java.util.Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                step();
            }
        }, 0, stepDelay);
        running = true;
    }

    /**
     * Stops the autoStep timer. If autoStep is not running, this method does nothing.
     */
    public void stopAutoStep() {
        if (timer != null) {
            timer.cancel();
        }
        running = false;
    }
}
