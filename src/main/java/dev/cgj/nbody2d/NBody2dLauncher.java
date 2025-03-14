package dev.cgj.nbody2d;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;

import static dev.cgj.nbody2d.NBody2d.*;

@Slf4j
public class NBody2dLauncher {

    /**
     * Main method which creates and configures a {@link NBody2d} simulation.
     *
     * @param args if an integer is passed as an argument then it will determine the 'n' parameter.
     */
    public static void main(String[] args) {
        NBody2dConfiguration config = readConfiguration();
        log.info("Starting simulation with n={} bodies", config.getNBodies());

        // create and configure the simulation
        NBody2d sim = new NBody2d(MARS_DIST, 3600, config.getNBodies(), EARTH_MASS, EARTH_RADIUS);
        sim.autoStep(1000 / 60);

        // create a window to view the simulation state
        NBody2dViewer viewer = new NBody2dViewer(sim);
        viewer.run();
    }

    /**
     * Reads config.yml from resources and deserializes as {@link NBody2dConfiguration}.
     */
    private static NBody2dConfiguration readConfiguration() {
        ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
        try {
            URL resource = NBody2dLauncher.class.getClassLoader().getResource("config.yml");
            String configPath = resource.getPath();
            return mapper.readValue(Files.readString(Paths.get(configPath)), NBody2dConfiguration.class);
        } catch (IOException e) {
            throw new RuntimeException("Failed to read configuration", e);
        }
    }
}
