package dev.cgj.nbody2d.data;

import dev.cgj.nbody2d.protobuf.Definition.Vec2Proto;
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

    public Vec2 randomOffset(double limit) {
        if (limit < 0) {
            throw new IllegalArgumentException("limit must be greater than or equal to 0");
        }

        if (limit == 0) {
            return this;
        }

        // pick a random angle in [0, 2pi) and a random distance in [0, limit)
        double angle = Math.random() * (2 * Math.PI);
        double distance = Math.pow(Math.random(), 0.5) * limit;

        // calculate (x,y) coordinate of this point and assign to current body
        return new Vec2(
            Math.cos(angle) * distance,
            Math.sin(angle) * distance
        );
    }

    public double distanceFrom(Vec2 other) {
        return add(other).magnitude();
    }

    public Vec2Proto toProto() {
        return Vec2Proto.newBuilder().setX(x).setY(y).build();
    }

    public static Vec2 fromProto(Vec2Proto position) {
        return new Vec2(position.getX(), position.getY());
    }
}
