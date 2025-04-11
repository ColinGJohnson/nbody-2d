package dev.cgj.nbody2d.data;

import dev.cgj.nbody2d.protobuf.Body;

import java.util.List;

public record RecordedSimulation(List<SimulationFrame> frames) {
    public static RecordedSimulation fromProto(Body.RecordedSimulationProto proto) {
        List<SimulationFrame> frames = proto.getFramesList().stream()
                .map(SimulationFrame::fromProto)
                .toList();
        return new RecordedSimulation(frames);
    }
}
