package dev.cgj.nbody2d.data;

import dev.cgj.nbody2d.protobuf.Body.SimulationFrameProto;

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
}
