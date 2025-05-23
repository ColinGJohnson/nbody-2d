package dev.cgj.nbody2d.simulation;

import dev.cgj.nbody2d.data.Body;
import dev.cgj.nbody2d.data.SimulationFrame;
import dev.cgj.nbody2d.data.Vec2;

import java.util.Comparator;
import java.util.List;
import java.util.Map;

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

    SimulationFrame currentFrame();

    Map<String, List<Body>> getHistory(int n);

    long getTimeElapsed();

    double getBoundary();

    static Body nearestBody(Simulation simulation, Vec2 position) {
        return simulation.currentFrame().bodies().stream()
            .min(Comparator.comparingDouble(body ->
                body.getPosition().distanceFrom(position)))
            .orElseThrow(() -> new IllegalStateException("No nearby bodies exist"));
    }
}
