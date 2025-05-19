package dev.cgj.nbody2d.data;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.cgj.nbody2d.config.SimulationConfig;
import dev.cgj.nbody2d.protobuf.Definition.SimulationHistoryProto;

import java.util.List;

public record SimulationHistory(List<SimulationFrame> frames, SimulationConfig config) {
    private static final ObjectMapper MAPPER = new ObjectMapper();

    public static SimulationHistory fromProto(SimulationHistoryProto proto) throws JsonProcessingException {
        List<SimulationFrame> frames = proto.getFramesList().stream()
            .map(SimulationFrame::fromProto)
            .toList();
        return new SimulationHistory(frames, MAPPER.readValue(proto.getConfigYaml(), SimulationConfig.class));
    }

    public SimulationHistoryProto toProto() throws JsonProcessingException {
        return SimulationHistoryProto.newBuilder()
            .addAllFrames(frames.stream()
                .map(SimulationFrame::toProto)
                .toList())
            .setConfigYaml(MAPPER.writeValueAsString(config))
            .build();
    }
}
