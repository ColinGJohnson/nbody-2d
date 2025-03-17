package dev.cgj.nbody2d.config;

import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

import java.util.List;

@Value
@Builder
@Jacksonized
public class SimulationConfig {

    /**
     * The edge of this simulation's universe.
     */
    double boundary;

    /**
     * The amount of simulated time between steps (seconds).
     */
    double dt;

    List<InitialBodyConfig> initialState;
}
