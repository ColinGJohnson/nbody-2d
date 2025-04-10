package dev.cgj.nbody2d.data;

import lombok.Data;

import java.util.List;

@Data
public class RecordedSimulation {
    List<SimulationFrame> frames;
}
