package dev.cgj.nbody2d;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import dev.cgj.nbody2d.config.Config;
import dev.cgj.nbody2d.simulation.SimulationBody;
import dev.cgj.nbody2d.data.Body;
import dev.cgj.nbody2d.simulation.Simulation;
import lombok.extern.slf4j.Slf4j;
import picocli.CommandLine;

import java.io.File;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

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

        // create and configure the simulation
        Simulation sim = new Simulation(config.getSimulation());
        log.info("Created simulation with n={} bodies", sim.getBodies().size());

        // create a window to view the simulation state
        if (headless) {
            runHeadless(sim);
        } else {
            runGui(config, sim);
        }
    }

    private void runGui(Config config, Simulation sim) {
        NBody2dViewer viewer = new NBody2dViewer(config.getViewer(), sim);
        viewer.run();
    }

    private void runHeadless(Simulation sim) {
        log.info("Running simulation headless for {} steps", steps);

        List<List<Body>> history = new ArrayList<>();
        for (int i = 0; i < steps; i++) {
            sim.step();
            history.add(sim.getBodies().stream().map(SimulationBody::getState).toList());
        }

        ObjectMapper mapper = new ObjectMapper();
        try {
            File file = new File(outputPath);
            mapper.writeValue(file, history);
            log.info("Simulation results written to {}", outputPath);
        } catch (Exception e) {
            log.error("Failed to write simulation results to file", e);
        }
    }

    /**
     * Main method which creates and configures a {@link Simulation} simulation.
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
            String configPath = resource.getPath();
            String configYaml = Files.readString(Paths.get(configPath));
            return mapper.readValue(configYaml, Config.class);
        } catch (Exception e) {
            log.error("Failed to read configuration", e);
            System.exit(1);
            return null;
        }
    }
}
