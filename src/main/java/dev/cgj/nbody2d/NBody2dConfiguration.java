package dev.cgj.nbody2d;

import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

@Value
@Builder
@Jacksonized
public class NBody2dConfiguration {
    Integer nBodies;
    Integer fpsTarget;
}
