package dev.cgj.nbody2d.simulation;

import dev.cgj.nbody2d.data.RecordedSimulation;
import dev.cgj.nbody2d.data.SimulationFrame;

import java.util.List;

public class ReplaySimulation implements Simulation {
    private final RecordedSimulation recordedSimulation;
    private final List<SimulationBody> bodies;
    private int frameIndex = 0;

    public ReplaySimulation(RecordedSimulation recordedSimulation) {
        this.recordedSimulation = recordedSimulation;
        SimulationFrame frame = recordedSimulation.frames().getFirst();
        bodies = frame.bodies().stream().map(SimulationBody::new).toList();
    }

    @Override
    public List<SimulationBody> getBodies() {
        SimulationFrame frame = recordedSimulation.frames().get(frameIndex);
        double maxForce = frame.getMaxForce();

        for (int i = 0; i < frame.bodies().size(); i++) {
            bodies.get(i).pushState(frame.bodies().get(i));
            bodies.get(i).updateColor(maxForce);
        }

        return bodies;
    }

    @Override
    public void reset() {
        frameIndex = 0;
    }

    @Override
    public void step() {
        frameIndex = (frameIndex + 1) % recordedSimulation.frames().size();
    }

    @Override
    public long getTimeElapsed() {
        return (long)(frameIndex * recordedSimulation.config().getDt());
    }

    @Override
    public double getBoundary() {
        return recordedSimulation.config().getBoundary();
    }
}
