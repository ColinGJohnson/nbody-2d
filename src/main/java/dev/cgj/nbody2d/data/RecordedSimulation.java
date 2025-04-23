package dev.cgj.nbody2d.data;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.cgj.nbody2d.config.SimulationConfig;
import dev.cgj.nbody2d.protobuf.Body.RecordedSimulationProto;

import java.util.List;

public record RecordedSimulation(List<SimulationFrame> frames, SimulationConfig config) {
    private static final ObjectMapper MAPPER = new ObjectMapper();

    public static RecordedSimulation fromProto(RecordedSimulationProto proto) throws JsonProcessingException {
        List<SimulationFrame> frames = proto.getFramesList().stream()
            .map(SimulationFrame::fromProto)
            .toList();
        return new RecordedSimulation(frames, MAPPER.readValue(proto.getConfigYaml(), SimulationConfig.class));
    }

    public RecordedSimulationProto toProto() throws JsonProcessingException {
        return RecordedSimulationProto.newBuilder()
            .addAllFrames(frames.stream()
                .map(SimulationFrame::toProto)
                .toList())
            .setConfigYaml(MAPPER.writeValueAsString(config))
            .build();
    }
}
