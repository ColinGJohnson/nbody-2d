package dev.cgj.nbody2d.data;

import dev.cgj.nbody2d.protobuf.Definition.SimulationFrameProto;

import java.util.List;

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
}
