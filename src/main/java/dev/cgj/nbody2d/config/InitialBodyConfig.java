package dev.cgj.nbody2d.config;

import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

@Value
@Builder
@Jacksonized
public class InitialBodyConfig {
    int n;

    /**
     * Initial x-distance from the origin (in meters). May be altered by {@link #positionJitter}.
     */
    double x;

    /**
     * Initial y-distance from the origin (in meters). May be altered by {@link #positionJitter}.
     */
    double y;

    /**
     * Variation in initial position. When added to the simulation, the body will be offset between
     * 0 and positionJitter meters in a random direction.
     */
    double positionJitter;

    /**
     * Initial x-velocity (in meters per second)
     */
    double vx;

    /**
     * Initial y-velocity (in meters per second).
     */
    double vy;

    /**
     * Variation in initial velocity. Applied similarly to {@link #positionJitter}.
     */
    double velocityJitter;

    /**
     * Physical radius of this body (in meters).
     */
    double r;

    /**
     * Variation in initial radius. Applied similarly to {@link #positionJitter}.
     */
    double radiusJitter;

    /**
     * Initial mass for the body (in kilograms).
     */
    double mass;
}
