package dev.cgj.nbody2d.simulation;

import dev.cgj.nbody2d.data.RecordedSimulation;
import dev.cgj.nbody2d.data.SimulationFrame;

import java.util.List;

public class ReplaySimulation implements Simulation {
    private final RecordedSimulation recordedSimulation;
    private int frameIndex = 0;

    public ReplaySimulation(RecordedSimulation recordedSimulation) {
        this.recordedSimulation = recordedSimulation;
    }

    @Override
    public List<SimulationBody> getBodies() {
        SimulationFrame frame = recordedSimulation.getFrames().get(frameIndex);
        return frame.getBodies().stream()
            .map(SimulationBody::new)
            .toList();
    }

    @Override
    public void reset() {
        frameIndex = 0;
    }

    @Override
    public void step() {
        frameIndex += 1;
    }

    @Override
    public long getTimeElapsed() {
        return 0;
    }

    @Override
    public double getBoundary() {
        return 0;
    }
}
