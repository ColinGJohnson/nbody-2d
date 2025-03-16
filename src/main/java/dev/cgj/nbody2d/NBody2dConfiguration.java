package dev.cgj.nbody2d;

import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

import java.util.List;

@Value
@Builder
@Jacksonized
public class NBody2dConfiguration {
    Integer nBodies;
    Integer fpsTarget;
    List<Body2dState> bodies;
}
