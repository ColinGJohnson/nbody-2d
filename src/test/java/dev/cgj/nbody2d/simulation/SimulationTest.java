package dev.cgj.nbody2d.simulation;

import dev.cgj.nbody2d.data.Body;
import dev.cgj.nbody2d.data.Vec2;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class SimulationTest {

    @Test
    void nearestBody_returnsClosestBody_whenMultipleBodiesExist() {
        Vec2 position = new Vec2(0, 0);
        SimulationBody body1 = new SimulationBody(createBody(new Vec2(5, 5), 1.0));
        SimulationBody body2 = new SimulationBody(createBody(new Vec2(2, 2), 2.0));
        SimulationBody body3 = new SimulationBody(createBody(new Vec2(10, 10), 3.0));
        Simulation simulation = new MockSimulation(List.of(body1, body2, body3));

        SimulationBody result = simulation.nearestBody(position);

        assertEquals(body2, result);
    }

    @Test
    void nearestBody_returnsOnlyBody_whenSingleBodyExists() {
        Vec2 position = new Vec2(0, 0);
        SimulationBody body = new SimulationBody(createBody(new Vec2(3, 3), 1.0));
        Simulation simulation = new MockSimulation(Collections.singletonList(body));

        SimulationBody result = simulation.nearestBody(position);

        assertEquals(body, result);
    }

    @Test
    void nearestBody_throwsException_whenNoBodiesExist() {
        Vec2 position = new Vec2(0, 0);
        Simulation simulation = new MockSimulation(Collections.emptyList());

        assertThrows(IllegalStateException.class, () -> simulation.nearestBody(position));
    }

    private Body createBody(Vec2 position, double mass) {
        return Body.builder()
            .position(position)
            .velocity(Vec2.ZERO)
            .force(Vec2.ZERO)
            .radius(1.0)
            .mass(mass)
            .build();
    }

    /**
     * Mock implementation of the Simulation interface for testing purposes.
     */
    private record MockSimulation(List<SimulationBody> bodies) implements Simulation {

        @Override
        public List<SimulationBody> getBodies() {
            return bodies;
        }

        @Override
        public void reset() { }

        @Override
        public void step() { }

        @Override
        public long getTimeElapsed() {
            return 0;
        }

        @Override
        public double getBoundary() {
            return 0;
        }
    }
}