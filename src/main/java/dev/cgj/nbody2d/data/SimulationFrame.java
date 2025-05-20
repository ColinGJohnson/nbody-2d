package dev.cgj.nbody2d.data;

import dev.cgj.nbody2d.protobuf.Definition.SimulationFrameProto;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

public record SimulationFrame(List<Body> bodies) {
    public static SimulationFrame fromProto(SimulationFrameProto proto) {
        List<Body> bodies = proto.getBodiesList().stream()
                .map(Body::fromProto)
                .toList();
        return new SimulationFrame(bodies);
    }

    public SimulationFrameProto toProto() {
        SimulationFrameProto.Builder builder = SimulationFrameProto.newBuilder();
        builder.addAllBodies(bodies.stream().map(Body::toProto).toList());
        return builder.build();
    }

    /**
     * Get the maximum force acting on any {@link Body} in this frame.
     *
     * @return The maximum force in Newtons.
     */
    public double getMaxForce() {
        double maxForce = 0;
        for (Body body : bodies()) {
            double currentForce = body.getForce().magnitude();
            if (currentForce > maxForce) maxForce = currentForce;
        }
        return maxForce;
    }

    /**
     * Get the maximum velocity of any {@link Body} in this frame.
     *
     * @return The maximum velocity in meters per second
     */
    public double getMaxVelocity() {
        double maxVelocity = 0;
        for (Body body : bodies()) {
            double currentVelocity = body.getVelocity().magnitude();
            if (currentVelocity > maxVelocity) maxVelocity = currentVelocity;
        }
        return maxVelocity;
    }

    public Optional<Body> getById(String id) {
        return bodies.stream()
            .filter(body -> Objects.equals(body.getId(), id))
            .findFirst();
    }
}
