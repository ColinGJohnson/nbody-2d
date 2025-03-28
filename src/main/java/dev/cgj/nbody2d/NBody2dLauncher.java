package dev.cgj.nbody2d;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import dev.cgj.nbody2d.config.Config;
import dev.cgj.nbody2d.simulation.Simulation;
import lombok.extern.slf4j.Slf4j;
import picocli.CommandLine;

import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;

import static picocli.CommandLine.Command;
import static picocli.CommandLine.Option;

@Slf4j
@Command(name = "NBody2D", mixinStandardHelpOptions = true)
public class NBody2dLauncher implements Runnable {

    @Option(names = { "-c", "--output" }, description = "Config file")
    String configurationPath = "config.yml";

    @Option(names = { "h", "--headless" }, description = "Whether ")
    boolean headless = false;

    @Override
    public void run() {
        log.info("Reading configuration from {}", configurationPath);
        Config config = readConfiguration(configurationPath);

        // create and configure the simulation
        Simulation sim = new Simulation(config.getSimulation());
        log.info("Created simulation with n={} bodies", sim.getBodies().size());

        // create a window to view the simulation state
        if (headless) {
            runHeadless(config, sim);
        } else {
            runGui(config, sim);
        }
    }

    private void runGui(Config config, Simulation sim) {
        NBody2dViewer viewer = new NBody2dViewer(config.getViewer(), sim);
        viewer.run();
    }

    private void runHeadless(Config config, Simulation sim) {
        throw new UnsupportedOperationException("Not yet implemented");
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
