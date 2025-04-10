package dev.cgj.nbody2d.simulation;

import dev.cgj.nbody2d.config.InitialBodyConfig;
import dev.cgj.nbody2d.config.SimulationConfig;
import dev.cgj.nbody2d.data.Body;
import dev.cgj.nbody2d.data.Vec2;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

/**
 * Brute-force 2-dimensional Newtonian Gravity n-body simulation.
 */
@Slf4j
@Getter
public class RealTimeSimulation implements Simulation {

    private final SimulationConfig config;

    /**
     * An list of {@link SimulationBody} objects representing the bodies participating in the simulation.
     * Each body tracks its position, velocity, and the forces acting upon it. These objects
     * are used to model the gravitational interactions between the bodies in the N-body simulation.
     */
    private List<SimulationBody> bodies;

    /**
     * The amount of simulated time that has passed so far (seconds).
     */
    @Setter
    private long timeElapsed;

    /**
     * NBody2d Constructor.
     *
     * @param config Simulation configuration including boundary, deta time, and initial bodies.
     */
    public RealTimeSimulation(SimulationConfig config) {
        this.config = config;
        reset();
    }

    public void reset() {
        int n = config.getInitialState().stream()
            .mapToInt(InitialBodyConfig::getN)
            .sum();
        bodies = new ArrayList<>(n);

        for (InitialBodyConfig init : config.getInitialState()) {
            for (int j = 0; j < init.getN(); j++) {
                SimulationBody body = new SimulationBody(Body.builder()
                    .position(new Vec2(init.getX(), init.getY()))
                    .velocity(new Vec2(init.getVx(), init.getVy()))
                    .force(Vec2.ZERO)
                    .radius(init.getR())
                    .mass(init.getMass())
                    .build());
                randomizePosition(body, init.getPositionJitter());
                bodies.add(body);
            }
        }
    }

    /**
     * Moves the given body in a random direction within 'limit' meters of its original position.
     */
    public void randomizePosition(SimulationBody body, double limit) {
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
        body.setState(body.getState().withPosition(new Vec2(
                Math.cos(angle) * distance,
                Math.sin(angle) * distance)
        ));
    }

    /**
     * Get the maximum force acting on any {@link SimulationBody} in the simulation.
     *
     * @return The maximum force in Newtons.
     */
    public double getMaxForce() {
        double maxForce = 0;
        for (SimulationBody body : bodies) {
            double currentForce = body.getState().getForce().magnitude();
            if (currentForce > maxForce) maxForce = currentForce;
        }
        return maxForce;
    }

    /**
     * Advances the simulation by one time step.
     */
    public void step() {
        List<SimulationBody> active = bodies.stream().filter(SimulationBody::isActive).toList();

        // update the forces acting on each body
        for (SimulationBody body : active) {
            body.updateForces(active);
        }

        // update the positions and colors of each body
        double maxForce = getMaxForce();
        for (SimulationBody body : active) {
            body.updateVelocity(config.getDt());
            body.updateColor(maxForce);
            body.updatePosition(config.getDt());
            body.applyBoundary(config.getBoundaryType(), config.getBoundary());
        }

        timeElapsed += (long) config.getDt();
    }

    @Override
    public double getBoundary() {
        return config.getBoundary();
    }
}
