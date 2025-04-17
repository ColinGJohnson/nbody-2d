package dev.cgj.nbody2d.data;

import dev.cgj.nbody2d.config.SimulationConfig;
import dev.cgj.nbody2d.protobuf.Body.RecordedSimulationProto;

import java.util.List;

public record RecordedSimulation(List<SimulationFrame> frames, SimulationConfig config) {
    public static RecordedSimulation fromProto(RecordedSimulationProto proto) {
        List<SimulationFrame> frames = proto.getFramesList().stream()
            .map(SimulationFrame::fromProto)
            .toList();
        SimulationConfig config = SimulationConfig.builder()
            .boundary(proto.getBoundary())
            .dt(proto.getDt())
            .build();
        return new RecordedSimulation(frames, config);
    }

    public RecordedSimulationProto toProto() {
        return RecordedSimulationProto.newBuilder()
            .addAllFrames(frames.stream()
                .map(SimulationFrame::toProto)
                .toList())
            .setBoundary(config.getBoundary())
            .setDt(config.getDt())
            .build();
    }
}
