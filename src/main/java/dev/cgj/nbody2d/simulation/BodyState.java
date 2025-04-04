package dev.cgj.nbody2d.simulation;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Builder;
import lombok.Value;
import lombok.With;
import lombok.extern.jackson.Jacksonized;

import java.awt.Color;

@Value
@Builder
@With
@Jacksonized
public class BodyState {

    /**
     * Distance from the origin in meters.
     */
    Vec2 position;

    /**
     * Velocity in meters per second.
     */
    Vec2 velocity;

    /**
     * Force in Newtons.
     */
    Vec2 force;

    /**
     * Physical radius of this body (in meters).
     */
    double radius;

    /**
     * The mass of this body (in kilograms).
     */
    double mass;

    /**
     * The color of this body (not used in calculations).
     */
    @JsonIgnore
    Color color;
}
