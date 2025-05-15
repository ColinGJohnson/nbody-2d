package dev.cgj.nbody2d.simulation;

import dev.cgj.nbody2d.data.Vec2;

import java.util.Comparator;
import java.util.List;

public interface Simulation {

    /**
     * Softening parameter (epsilon) determining the minimum distance between this body and another
     * when calculating forces to avoid infinite forces at very short distances.
     */
    double EPS = 3E4;

    /**
     * Universal gravitational constant.
     */
    double G = 6.673e-11;

    void reset();

    void step();

    List<SimulationBody> getBodies();

    long getTimeElapsed();

    double getBoundary();

    default SimulationBody nearestBody(Vec2 position) {
        return getBodies().stream()
            .min(Comparator.comparingDouble(body ->
                body.getState().getPosition().distanceFrom(position)))
            .orElseThrow(() -> new IllegalStateException("Simulation contains "));
    }
}
