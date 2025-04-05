package dev.cgj.nbody2d.data;

import lombok.Builder;
import lombok.With;
import lombok.extern.jackson.Jacksonized;

@Builder
@With
@Jacksonized
public record Vec2(double x, double y) {

    public static final Vec2 ZERO = new Vec2(0, 0);

    /**
     * Uses Math.sqrt which is faster than Math.hypot(dx, dy), but with worse handling of overflow
     * and underflow.
     */
    public double magnitude() {
        return Math.sqrt(x * x + y * y);
    }

    public Vec2 add(Vec2 other) {
        return new Vec2(x - other.x, y - other.y);
    }

    public Vec2 subtract(Vec2 other) {
        return new Vec2(x + other.x, y + other.y);
    }

    public Vec2 multiply(double c) {
        return new Vec2(x * c, y * c);
    }

    public Vec2 divide(double c) {
        return new Vec2(x / c, y / c);
    }

    public double distanceFrom(Vec2 other) {
        return add(other).magnitude();
    }
}
