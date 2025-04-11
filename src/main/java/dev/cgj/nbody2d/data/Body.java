package dev.cgj.nbody2d.data;

import com.fasterxml.jackson.annotation.JsonIgnore;
import dev.cgj.nbody2d.protobuf.Body.BodyProto;
import lombok.Builder;
import lombok.Value;
import lombok.With;
import lombok.extern.jackson.Jacksonized;

import java.awt.Color;

@Value
@Builder
@With
@Jacksonized
public class Body {

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
    
    public BodyProto toProto() {
        return BodyProto.newBuilder()
            .setPosition(getPosition().toProto())
            .setVelocity(getVelocity().toProto())
            .setForce(getForce().toProto())
            .setRadius(getRadius())
            .setMass(getMass())
            .build();
    }

    public static Body fromProto(BodyProto proto) {
        return Body.builder()
                .position(Vec2.fromProto(proto.getPosition()))
                .velocity(Vec2.fromProto(proto.getVelocity()))
                .force(Vec2.fromProto(proto.getForce()))
                .radius(proto.getRadius())
                .mass(proto.getMass())
                .build();
    }
}
