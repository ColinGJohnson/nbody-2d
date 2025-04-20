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
    private List<SimulationBody> bodies;

    /**
     * The amount of simulated time that has passed so far (seconds).
     */
    @Setter
    private long timeElapsed;

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
                randomizeVelocity(body, init.getVelocityJitter());
                bodies.add(body);
            }
        }
    }

    /**
     * Randomizes the position of the given {@code SimulationBody} within a specified limit.
     * The new position is calculated by applying a random offset to the current position of the body.
     *
     * @param body the {@code SimulationBody} whose position will be randomized
     * @param limit the maximum magnitude of the random offset to be applied to the body's position
     */
    public void randomizePosition(SimulationBody body, double limit) {
        Vec2 offsetPosition = body.getState().getPosition().randomOffset(limit);
        body.setState(body.getState().withPosition(offsetPosition));
    }

    /**
     * Randomizes the velocity of a given simulation body by applying a random offset
     * within the specified limit.
     *
     * @param body the {@code SimulationBody} whose velocity will be randomized
     * @param limit the maximum magnitude of the random offset to be applied to the body's velocity
     */
    public void randomizeVelocity(SimulationBody body, double limit) {
        Vec2 offsetVelocity = body.getState().getVelocity().randomOffset(limit);
        body.setState(body.getState().withVelocity(offsetVelocity));
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
