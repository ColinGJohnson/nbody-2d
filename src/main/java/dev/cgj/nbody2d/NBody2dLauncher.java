package dev.cgj.nbody2d;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import dev.cgj.nbody2d.config.Config;
import dev.cgj.nbody2d.simulation.Simulation;
import lombok.extern.slf4j.Slf4j;

import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;

@Slf4j
public class NBody2dLauncher {
    public static final String CONFIG_FILE = "config.yml";

    /**
     * Main method which creates and configures a {@link Simulation} simulation.
     *
     * @param args if an integer is passed as an argument then it will determine the 'n' parameter.
     */
    public static void main(String[] args) {
        log.info("Reading configuration from {}", CONFIG_FILE);
        Config config = readConfiguration(CONFIG_FILE);

        // create and configure the simulation
        Simulation sim = new Simulation(config.getSimulation());
        log.info("Created simulation with n={} bodies", sim.getBodies().length);

        // create a window to view the simulation state
        NBody2dViewer viewer = new NBody2dViewer(config.getViewer(), sim);
        viewer.run();
    }

    /**
     * Reads config.yml from resources and deserializes as {@link Config}.
     * @param name
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
