package dev.cgj.nbody2d;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import dev.cgj.nbody2d.config.Config;
import dev.cgj.nbody2d.config.ViewerConfig;
import dev.cgj.nbody2d.data.RecordedSimulation;
import dev.cgj.nbody2d.protobuf.Body.RecordedSimulationProto;
import dev.cgj.nbody2d.protobuf.Body.SimulationFrameProto;
import dev.cgj.nbody2d.simulation.ReplaySimulation;
import dev.cgj.nbody2d.simulation.Simulation;
import dev.cgj.nbody2d.simulation.SimulationBody;
import dev.cgj.nbody2d.data.Body;
import dev.cgj.nbody2d.simulation.RealTimeSimulation;
import dev.cgj.nbody2d.viewer.Viewer;
import lombok.extern.slf4j.Slf4j;
import picocli.CommandLine;

import java.io.OutputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static picocli.CommandLine.Command;
import static picocli.CommandLine.Option;

@Slf4j
@Command(name = "NBody2D", mixinStandardHelpOptions = true)
public class NBody2dLauncher implements Runnable {

    @Option(names = {"-c", "--config"},
            description = "Path to the YAML configuration file. Defaults to 'uniform.yml'.")
    String configurationPath = "uniform.yml";

    @Option(names = {"-o", "--output"},
            description = "Path where the simulation results will be written. Defaults to 'output.yml'.")
    String outputPath = "output.yml";

    @Option(names = {"-i", "--input"},
            description = "Input path for a precalculated simulation.")
    String inputPath;

    @Option(names = {"--headless"},
            description = "Whether to run the simulation in headless mode (no GUI)")
    boolean headless = false;

    @Option(names = {"-s", "--steps"},
            description = "Number of simulation steps to run in headless mode. Ignored if not in headless mode.")
    int steps = 1000;

    @Override
    public void run() {
        log.info("Reading configuration from {}", configurationPath);
        Config config = readConfiguration(configurationPath);

        if (inputPath == null) {
            RealTimeSimulation sim = new RealTimeSimulation(config.getSimulation());
            log.info("Created real time simulation with n={} bodies", sim.getBodies().size());

            if (headless) {
                runHeadless(sim);
            } else {
                runViewer(config.getViewer(), sim);
            }
        } else {
            log.info("Replaying simulation from {}", inputPath);
            runViewer(config.getViewer(), new ReplaySimulation(readRecordedSimulation(inputPath)));
        }
    }

    private void runViewer(ViewerConfig config, Simulation simulation) {
        Viewer viewer = new Viewer(config, simulation);
        viewer.run();
    }

    private void runHeadless(RealTimeSimulation sim) {
        log.info("Running simulation headless for {} steps", steps);

        List<List<Body>> history = new ArrayList<>();
        for (int i = 0; i < steps; i++) {
            sim.step();
            history.add(sim.getBodies().stream().map(SimulationBody::getState).toList());
        }

        List<SimulationFrameProto> frames = history.stream()
            .map(frame -> SimulationFrameProto.newBuilder()
                .addAllBodies(frame.stream().map(Body::toProto).toList())
                .build())
            .toList();
        RecordedSimulationProto record = RecordedSimulationProto.newBuilder()
            .addAllFrames(frames)
            .build();

        try (OutputStream stream = Files.newOutputStream(Paths.get(outputPath))) {
            record.writeTo(stream);
            log.info("Simulation results written to {}", outputPath);
        } catch (Exception e) {
            log.error("Failed to write simulation results to file", e);
        }
    }

    /**
     * Main method which creates and configures a {@link RealTimeSimulation} simulation.
     *
     * @param args if an integer is passed as an argument then it will determine the 'n' parameter.
     */
    public static void main(String[] args) {
        new CommandLine(new NBody2dLauncher()).execute(args);
    }

    /**
     * Reads YAML config file from resources and deserializes as {@link Config}.
     */
    private static Config readConfiguration(String name) {
        ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
        try {
            URL resource = NBody2dLauncher.class.getClassLoader().getResource(name);
            String configPath = Objects.requireNonNull(resource).getPath();
            String configYaml = Files.readString(Paths.get(configPath));
            return mapper.readValue(configYaml, Config.class);
        } catch (Exception e) {
            log.error("Failed to read configuration", e);
            System.exit(1);
            return null;
        }
    }

    private static RecordedSimulation readRecordedSimulation(String inputPath) {
        try {
            byte[] data = Files.readAllBytes(Paths.get(inputPath));
            return RecordedSimulation.fromProto(RecordedSimulationProto.parseFrom(data));
        } catch (Exception e) {
            log.error("Failed to read recorded simulation", e);
            throw new RuntimeException(e);
        }
    }
}
