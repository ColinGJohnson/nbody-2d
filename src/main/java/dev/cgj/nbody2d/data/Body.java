package dev.cgj.nbody2d.data;

import dev.cgj.nbody2d.protobuf.Definition.BodyProto;
import lombok.Builder;
import lombok.Value;
import lombok.With;
import lombok.extern.jackson.Jacksonized;

@Value
@Builder
@With
@Jacksonized
public class Body {

    /**
     * ID for this body. This must be unique within a {@link SimulationFrame}, but should match for
     * the same body across successive frames.
     */
    String id;

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
     * Updates the velocity of this body given the forces currently acting on it. Does not affect
     * position or forces.
     *
     * @param dt delta time; the length of time for which the acceleration produced by the current
     *           forces on this body should be applied
     */
    public Body updateVelocity(double dt) {
        Vec2 delta = getForce().multiply(dt).divide(getMass());
        return withVelocity(getVelocity().add(delta));
    }

    /**
     * Updates the position of this body given the current velocity. Does not affect forces or
     * velocity.
     *
     * @param dt delta time
     */
    public Body updatePosition(double dt) {
        Vec2 delta = getVelocity().multiply(dt);
        return withPosition(getPosition().add(delta));
    }

    /**
     * Determines whether this body overlaps with another body.
     *
     * @param other the other body to check for overlap
     * @return true if the bodies overlap, false otherwise
     */
    public boolean overlapsWith(Body other) {
        double distanceBetween = getPosition().distanceFrom(other.getPosition());
        return distanceBetween <= getRadius() + other.getRadius();
    }

    /**
     * Calculates the area of the circular body.
     *
     * @return the area of the body as a double, calculated as π * radius²
     */
    public double area() {
        return Math.PI * Math.pow(getRadius(), 2);
    }
    
    public BodyProto toProto() {
        return BodyProto.newBuilder()
            .setId(getId())
            .setPosition(getPosition().toProto())
            .setVelocity(getVelocity().toProto())
            .setForce(getForce().toProto())
            .setRadius(getRadius())
            .setMass(getMass())
            .build();
    }

    public static Body fromProto(BodyProto proto) {
        return Body.builder()
                .id(proto.getId())
                .position(Vec2.fromProto(proto.getPosition()))
                .velocity(Vec2.fromProto(proto.getVelocity()))
                .force(Vec2.fromProto(proto.getForce()))
                .radius(proto.getRadius())
                .mass(proto.getMass())
                .build();
    }
}
