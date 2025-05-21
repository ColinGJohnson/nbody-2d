package dev.cgj.nbody2d.simulation;

import dev.cgj.nbody2d.config.BoundaryType;
import dev.cgj.nbody2d.config.InitialBodyConfig;
import dev.cgj.nbody2d.config.SimulationConfig;
import dev.cgj.nbody2d.data.Body;
import dev.cgj.nbody2d.data.SimulationFrame;
import dev.cgj.nbody2d.data.Vec2;
import dev.cgj.nbody2d.util.BoundedQueue;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

/**
 * Brute-force 2-dimensional Newtonian Gravity n-body simulation.
 */
@Slf4j
@Getter
public class RealTimeSimulation implements Simulation {

    private final SimulationConfig config;
    private final int historyLength;

    private BoundedQueue<SimulationFrame> frames;
    private Set<String> inactiveBodiesIds;

    /**
     * The amount of simulated time that has passed so far (seconds).
     */
    @Setter
    private long timeElapsed;

    public RealTimeSimulation(SimulationConfig config, int historyLength) {
        this.config = config;
        this.historyLength = historyLength;
        reset();
    }

    public void reset() {
        int n = config.getInitialState().stream()
            .mapToInt(InitialBodyConfig::getN)
            .sum();
        inactiveBodiesIds = new HashSet<>(n);
        List<Body> bodies = new ArrayList<>(n);

        for (InitialBodyConfig init : config.getInitialState()) {
            for (int j = 0; j < init.getN(); j++) {
                Body body = Body.builder()
                    .id(UUID.randomUUID().toString())
                    .position(new Vec2(init.getX(), init.getY())
                        .randomOffset(init.getPositionJitter()))
                    .velocity(new Vec2(init.getVx(), init.getVy())
                        .randomOffset(init.getVelocityJitter()))
                    .force(Vec2.ZERO)
                    .radius(applyJitter(init.getR(), init.getRadiusJitter()))
                    .mass(applyJitter(init.getMass(), init.getMassJitter()))
                    .build();
                bodies.add(body);
            }
        }

        frames = new BoundedQueue<>(historyLength);
        frames.add(new SimulationFrame(bodies));
    }

    public double applyJitter(double value, double jitter) {
        if (jitter == 0) {
            return value;
        }

        if (Math.abs(jitter) >= value) {
            throw new IllegalArgumentException("Jitter must be less than value.");
        }

        return value + ThreadLocalRandom.current().nextDouble(-jitter, jitter);
    }

    /**
     * Advances the simulation by one time step.
     */
    @Override
    public void step() {
        double dt = config.getDt();
        List<Body> updatedBodies = currentFrame().bodies().stream()
            .filter(body -> !inactiveBodiesIds.contains(body.getId()))
            .map(body -> updateForces(body, currentFrame().bodies()))
            .map(body -> body.updateVelocity(dt).updatePosition(dt))
            .map(body -> applyBoundary(body, config.getBoundaryType(), config.getBoundary()))
            .toList();
        List<Body> mergedBodies = mergeOverlappingBodies(updatedBodies);
        frames.add(new SimulationFrame(mergedBodies));
        timeElapsed += (long) dt;
    }

    private List<Body> mergeOverlappingBodies(List<Body> bodies) {
        List<Body> result = new ArrayList<>();
        Set<String> mergedIds = new HashSet<>();

        for (int i = 0; i < bodies.size(); i++) {
            Body body = bodies.get(i);

            if (mergedIds.contains(body.getId())) {
                continue;
            }

            double totalMass = body.getMass();
            Vec2 weightedPosition = body.getPosition().multiply(body.getMass());
            Vec2 weightedVelocity = body.getVelocity().multiply(body.getMass());

            for (int j = i + 1; j < bodies.size(); j++) {
                Body other = bodies.get(j);

                if (body.overlapsWith(other)) {
                    totalMass += other.getMass();
                    weightedPosition = weightedPosition.add(other.getPosition().multiply(other.getMass()));
                    weightedVelocity = weightedVelocity.add(other.getVelocity().multiply(other.getMass()));
                    mergedIds.add(other.getId());
                }
            }

            Vec2 finalPosition = weightedPosition.divide(totalMass);
            Vec2 finalVelocity = weightedVelocity.divide(totalMass);

            result.add(body
                .withPosition(finalPosition)
                .withVelocity(finalVelocity)
                .withMass(totalMass)
            );
            mergedIds.add(body.getId());
        }

        return result;
    }

    @Override
    public SimulationFrame currentFrame() {
        return frames.peek();
    }

    @Override
    public Map<String, List<Body>> getHistory() {
        return frames.asList().stream()
            .flatMap(frame -> frame.bodies().stream())
            .collect(Collectors.groupingBy(Body::getId));
    }

    @Override
    public double getBoundary() {
        return config.getBoundary();
    }

    /**
     * Updates the forces currently acting on this body using Newtonian Gravity. Does not affect
     * position or velocity.
     *
     * @param body The body on which forces should be calculated
     * @param others Other bodies whose gravity should be considered.
     */
    public Body updateForces(Body body, List<Body> others) {
        Vec2 netForce = Vec2.ZERO;

        for (Body other : others) {

            // don't calculate the force due to gravity between two bodies which are the same.
            if (Objects.equals(body.getId(), other.getId())) {
                continue;
            }

            Vec2 F = calculateGravitationalForce(body, other);
            netForce = netForce.add(F);
        }

        return body.withForce(netForce);
    }

    /**
     * Calculates the <a href="https://en.wikipedia.org/wiki/Newton%27s_law_of_universal_gravitation">gravitational
     * force</a> exerted on a body by another body, including a softening parameter {@link #EPS} to avoid infinite
     * forces
     *
     * @param body The body on which the gravitational force is being calculated.
     * @param other The other body exerting the gravitational force.
     *
     * @return The gravitational force as a {@code Vec2} vector acting on {@code body} in Newtons.
     */
    private static Vec2 calculateGravitationalForce(Body body, Body other) {

        // The two bodies cannot be so close that they would overlap.
        double dist = Math.max(
            body.getRadius() + other.getRadius(),
            body.getPosition().distanceFrom(other.getPosition())
        );

        return other.getPosition().subtract(body.getPosition()).divide(dist)
            .multiply((RealTimeSimulation.G * body.getMass() * other.getMass()) /
                (dist * dist + RealTimeSimulation.EPS * RealTimeSimulation.EPS));
    }

    /**
     * If the body is more than {@code boundary} meters from the origin, place it on the boundary
     * and stop it.
     *
     * @param boundary Maximum distance from the origin for this body's position.
     */
    public Body applyBoundary(Body body, BoundaryType type, double boundary) {
        if (Objects.requireNonNull(type) == BoundaryType.NONE) {
            return body;
        }

        double fromOrigin = body.getPosition().magnitude();

        if (fromOrigin > boundary) {
            if (type == BoundaryType.STICK) {
                inactiveBodiesIds.add(body.getId());
            } else if (type == BoundaryType.WRAP) {
                boundary = -boundary;
            }

            body = body.withPosition(body.getPosition().divide(fromOrigin).multiply(boundary));

            if (type == BoundaryType.STOP) {
                return body.withVelocity(Vec2.ZERO);
            }
        }

        return body;
    }
}
