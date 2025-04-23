package dev.cgj.nbody2d;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import dev.cgj.nbody2d.config.Config;
import dev.cgj.nbody2d.config.ViewerConfig;
import dev.cgj.nbody2d.data.RecordedSimulation;
import dev.cgj.nbody2d.data.SimulationFrame;
import dev.cgj.nbody2d.protobuf.Definition.RecordedSimulationProto;
import dev.cgj.nbody2d.simulation.ReplaySimulation;
import dev.cgj.nbody2d.simulation.Simulation;
import dev.cgj.nbody2d.simulation.SimulationBody;
import dev.cgj.nbody2d.data.Body;
import dev.cgj.nbody2d.simulation.RealTimeSimulation;
import dev.cgj.nbody2d.viewer.Viewer;
import lombok.extern.slf4j.Slf4j;
import picocli.CommandLine;

import java.io.IOException;
import java.io.OutputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static picocli.CommandLine.Command;
import static picocli.CommandLine.Option;

@Slf4j
@Command(name = "NBody2D", mixinStandardHelpOptions = true)
public class NBody2dLauncher implements Runnable {

    @Option(names = {"-c", "--config"},
            description = "Path to the YAML configuration file. Defaults to 'uniform.yml'.")
    String configurationPath = "examples/uniform.yml";

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

        List<SimulationFrame> frames = new ArrayList<>();
        for (int i = 0; i < steps; i++) {
            sim.step();
            List<Body> bodies = sim.getBodies().stream().map(SimulationBody::getState).toList();
            frames.add(new SimulationFrame(bodies));
        }

        RecordedSimulation record = new RecordedSimulation(frames, sim.getConfig());

        try (OutputStream stream = Files.newOutputStream(Paths.get(outputPath))) {
            record.toProto().writeTo(stream);
            log.info("Simulation results written to {}", outputPath);
        } catch (IOException e) {
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
     * Reads and parses a configuration file from the specified path.
     *
     * <p>
     *   This method attempts to load the configuration from the file system
     *   or the classpath, parses it as a YAML file, and maps it to a Config object.
     * </p>
     *
     * @param path the file path or classpath location of the YAML configuration file
     * @return Config object from the give path.
     * @throws RuntimeException if the configuration cannot be read or parsed.
     */
    private static Config readConfiguration(String path) {
        ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
        return readFileSystemConfiguration(path)
            .or(() -> readClasspathConfiguration(path))
            .map(yaml -> {
                try {
                    return mapper.readValue(yaml, Config.class);
                } catch (JsonProcessingException e) {
                    log.warn("Failed to parse configuration", e);
                }
                return null;
            })
            .orElseThrow(() -> new RuntimeException("Failed to read configuration from " + path));
    }

    private static Optional<String> readFileSystemConfiguration(String path) {
        try {
            return Optional.of(Files.readString(Paths.get(path)));
        } catch (Exception e) {
            log.warn("Failed to read configuration from filesystem", e);
            return Optional.empty();
        }
    }

    private static Optional<String> readClasspathConfiguration(String name) {
        try {
            URL resource = NBody2dLauncher.class.getClassLoader().getResource(name);
            String configPath = Objects.requireNonNull(resource).getPath();
            return Optional.of(Files.readString(Paths.get(configPath)));
        } catch (Exception e) {
            log.warn("Failed to read configuration from classpath", e);
            return Optional.empty();
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
