package dev.cgj.nbody2d.simulation;

import dev.cgj.nbody2d.data.Body;
import dev.cgj.nbody2d.data.SimulationFrame;
import dev.cgj.nbody2d.data.Vec2;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collections;

import static dev.cgj.nbody2d.simulation.Simulation.nearestBody;
import static java.util.Collections.singletonList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class SimulationTest {

    @Mock
    Simulation simulation;

    @Test
    void nearestBody_returnsClosestBody_multipleBodies() {
        when(simulation.currentFrame()).thenReturn(new SimulationFrame(Arrays.asList(
            createBody("1", new Vec2(5, 5), 1.0),
            createBody("2", new Vec2(2, 2), 2.0),
            createBody("3", new Vec2(10, 10), 3.0)
        )));

        Body result = nearestBody(simulation, new Vec2(0, 0));

        assertEquals("2", result.getId());
    }

    @Test
    void nearestBody_returnsOnlyBody_singleBody() {
        Body body = createBody("1", new Vec2(3, 3), 1.0);
        when(simulation.currentFrame()).thenReturn(new SimulationFrame(singletonList(body)));

        Body result = nearestBody(simulation, Vec2.ZERO);

        assertEquals(body, result);

    }

    @Test
    void nearestBody_throwsException_noBodies() {
        when(simulation.currentFrame()).thenReturn(new SimulationFrame(Collections.emptyList()));
        assertThrows(IllegalStateException.class, () -> nearestBody(simulation, Vec2.ZERO));
    }

    private Body createBody(String id, Vec2 position, double mass) {
        return Body.builder()
            .id(id)
            .position(position)
            .velocity(Vec2.ZERO)
            .force(Vec2.ZERO)
            .radius(1.0)
            .mass(mass)
            .build();
    }
}
