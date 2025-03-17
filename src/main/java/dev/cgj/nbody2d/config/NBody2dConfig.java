package dev.cgj.nbody2d.config;

import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

@Value
@Builder
@Jacksonized
public class NBody2dConfig {
    ViewerConfig viewer;
    SimulationConfig simulation;
}
