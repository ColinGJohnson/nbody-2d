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
     * x-distance from the origin (in meters).
     */
    double x;

    /**
     * y-distance from the origin (in meters).
     */
    double y;

    /**
     * x-velocity (in meters per second).
     */
    double vx;

    /**
     * y-velocity (in meters per second).
     */
    double vy;

    /**
     * x-force (in Newtons).
     */
    double fx;

    /**
     * y-force (in Newtons).
     */
    double fy;

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
