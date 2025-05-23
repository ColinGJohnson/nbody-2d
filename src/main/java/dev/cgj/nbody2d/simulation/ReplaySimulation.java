package dev.cgj.nbody2d.simulation;

import dev.cgj.nbody2d.data.Body;
import dev.cgj.nbody2d.data.SimulationHistory;
import dev.cgj.nbody2d.data.SimulationFrame;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ReplaySimulation implements Simulation {
    private final SimulationHistory simulationHistory;
    private int frameIndex = 0;

    public ReplaySimulation(SimulationHistory simulationHistory) {
        this.simulationHistory = simulationHistory;
    }

    @Override
    public SimulationFrame currentFrame() {
        return simulationHistory.frames().get(frameIndex);
    }

    @Override
    public Map<String, List<Body>> getHistory(int n) {
        return simulationHistory.frames()
            .subList(Math.max(0, frameIndex - n), frameIndex + 1)
            .stream()
            .flatMap(frame -> frame.bodies().stream())
            .collect(Collectors.groupingBy(Body::getId));
    }

    @Override
    public void reset() {
        frameIndex = 0;
    }

    @Override
    public void step() {
        frameIndex = (frameIndex + 1) % simulationHistory.frames().size();
    }

    @Override
    public long getTimeElapsed() {
        return (long)(frameIndex * simulationHistory.config().getDt());
    }

    @Override
    public double getBoundary() {
        return simulationHistory.config().getBoundary();
    }
}
