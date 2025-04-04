package dev.cgj.nbody2d.simulation;

import lombok.Builder;
import lombok.Value;
import lombok.With;
import lombok.extern.jackson.Jacksonized;

@Value
@Builder
@With
@Jacksonized
public class Vec2 {
    public static final Vec2 ZERO = new Vec2(0, 0);

    double x, y;

    /**
     * Uses Math.sqrt which is faster than Math.hypot(dx, dy), but with worse handling of overflow
     * and underflow.
     */
    public double magnitude() {
        return Math.sqrt(x * x + y * y);
    }

    public Vec2 minus(Vec2 other) {
        return new Vec2(x - other.x, y - other.y);
    }

    public Vec2 plus(Vec2 other) {
        return new Vec2(x + other.x, y + other.y);
    }

    public Vec2 times(double c) {
        return new Vec2(x * c, y * c);
    }

    public Vec2 dividedBy(double c) {
        return new Vec2(x / c, y / c);
    }

    public double distanceFrom(Vec2 other) {
        return minus(other).magnitude();
    }
}
