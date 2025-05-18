package dev.cgj.nbody2d.viewer;

import dev.cgj.nbody2d.data.Body;
import dev.cgj.nbody2d.simulation.Simulation;
import dev.cgj.nbody2d.simulation.SimulationBody;
import dev.cgj.nbody2d.config.ViewerConfig;
import dev.cgj.nbody2d.data.Vec2;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.geom.Path2D;
import java.time.Duration;
import java.util.TimerTask;

/**
 * Panel to display and manipulates a 2-dimensional n-body simulation (NBody2d). A window
 * will be created immediately upon instantiation.
 */
public class Viewer extends JPanel {
    final ViewerConfig config;
    final Simulation sim;       // the simulation being displayed
    SimulationBody selection;

    long frameTime;             // how long it took to draw the last frame, in nanoseconds
    JFrame frame;               // the frame that the simulation is displayed in
    boolean fullScreen = false; // is the viewer full screen currently?
    double scale;               // simulation meters per on-screen pixel

    boolean historyTrails;      // should trails be drawn?
    boolean colorTrails;        // should trails be colored?
    boolean forceVectors;       // should force vectors be rendered?

    boolean isPanning = false;  // true if the user currently panning
    Point panStartMouse;        // mouse position at start of pan, in pixels
    Point panStart;             // pan position at start of pan, in pixels
    final Point pan;            // the current x and y distance panned from the origin, in pixels

    /**
     * Timer for autoStep().
     */
    java.util.Timer timer;

    /**
     * True when the simulation is automatically calling step().
     */
    boolean running;

    private long stepTime;

    /**
     * NBody2dViewer Constructor. Creates and configures display panel.
     *
     * @param config configuration for the viewer
     * @param sim the simulation to display
     */
    public Viewer(ViewerConfig config, Simulation sim) {
        super(true);
        this.config = config;
        this.sim = sim;
        this.pan = new Point(0, 0);

        InputHandler inputHandler = new InputHandler(this);
        addMouseListener(inputHandler);
        addMouseWheelListener(inputHandler);
        addKeyListener(inputHandler);

        setFocusable(true);
        requestFocusInWindow();

        SwingUtilities.invokeLater(() -> {
            createAndShowGUI(false);
            setScaleToFit();
        });
    }

    /**
     * Creates and configures a new JFrame containing a single {@link Viewer}.
     *
     * @param makeFullscreen determines whether the created window is full screen or not.
     */
    private void createAndShowGUI(boolean makeFullscreen) {
        frame = new JFrame("2D N-Body Viewer");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        if (makeFullscreen) {
            frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
            frame.setUndecorated(true);
        } else {
            frame.setSize(1280, 720);
            frame.setLocationRelativeTo(null);
        }

        frame.add(this);
        frame.setVisible(true);
    }

    public void selectClosest(Point point) {
        selection = sim.nearestBody(pixelsToSim(point));
    }

    public void clearSelection() {
        selection = null;
    }

    /**
     * Enables or disables full-screen mode. Questionable behavior on systems with multiple monitors.
     */
    public void toggleFullScreen() {
        frame.setVisible(false);
        frame.remove(this);
        frame.dispose();
        fullScreen = !fullScreen;
        createAndShowGUI(fullScreen);
        frame.setVisible(true);
    }

    /**
     * Calculates the scale required to fit the whole simulation space within the current window.
     */
    public double getScaleToFit() {
        double constraint = Math.min(frame.getWidth(), frame.getHeight());
        double boundary = sim.getBoundary();
        return (boundary * 2) / constraint;
    }

    /**
     * Resets the scale and pan to fit the whole simulation space within the current window.
     */
    public void setScaleToFit() {
        scale = getScaleToFit();
        pan.setLocation(0, 0);
    }

    /**
     * Get the center of the window in pixels from the top left of the window.
     * <p>
     * This is also the location where the origin (0,0) in the simulation should be drawn assuming
     * the user hasn't yet panned (panX = panY = 0).
     * </p>
     */
    private Point getScreenCenter() {
        return new Point(this.getWidth() / 2, this.getHeight() / 2);
    }

    /**
     * Converts an x or y coordinate using the simulation's coordinate system to a pixels
     * location on the screen (relative to the top left of the window).
     *
     * @param x the x-coordinate in the simulation to convert to pixels on the screen.
     * @param y the y-coordinate in the simulation to convert to pixels on the screen.
     * @return a Point containing the coordinates on the screen.
     */
    private Point simToPixels(double x, double y) {
        Point screenCenter = getScreenCenter();

        int pixelX = (screenCenter.x + pan.x) + (int) (x / scale);
        int pixelY = (screenCenter.y + pan.y) + (int) (y / scale);

        return new Point(pixelX, pixelY);
    }

    private Point simToPixels(Vec2 position) {
        return simToPixels(position.x(), position.y());
    }

    /**
     * Converts a pixel on the viewer window to the simulation's coordinate system.
     *
     * @param pixel Pixel coordinate relative to the top left of the window.
     * @return A double point containing the coordinates in the simulation.
     */
    Vec2 pixelsToSim(Point pixel) {
        Point screenCenter = getScreenCenter();
        double simX = (pixel.x - screenCenter.x - pan.x) * scale;
        double simY = (pixel.y - screenCenter.y - pan.y) * scale;
        return new Vec2(simX, simY);
    }

    /**
     * Converts a length measurement in the simulation to pixels using scale, for visualization
     *
     * @param distance the distance, in meters, from the simulation
     * @return a distance in pixels
     */
    int distanceToPixels(double distance) {
        long pixelDistance = Math.round(distance / scale);

        if (Math.abs(pixelDistance) > Integer.MAX_VALUE) {
            System.err.println("Warning: inaccurate conversion of long pixel distance to integer");
        }

        return (int) pixelDistance;
    }

    /**
     * Converts a number of seconds into a human-readable String of the form "__y __m __d __m __s".
     *
     * @param n a number of seconds
     * @return a human-readable string
     */
    public static String secondsToString(long n) {
        return Duration.ofSeconds(n).toString();
    }

    /**
     * Overrides the custom JComponent paint method to display custom graphics.
     *
     * @param g the graphics context to paint with
     */
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        long startTime = System.nanoTime();

        if (selection != null) {
            centerWindowOn(selection.getState().getPosition());
        }

        // dark background
        g.setColor(new Color(30, 30, 30));
        g.fillRect(0, 0, this.getWidth(), this.getHeight());

        // draw debug info
        g.setColor(Color.WHITE);
        g.drawString("tracked particles: " + sim.getBodies().size(), 20, 40);
        g.drawString(String.format("scale: %.2e meters / pixel", scale), 20, 55);
        g.drawString("sim elapsed time: " + secondsToString(sim.getTimeElapsed()), 20, 70);
        g.drawString("sim step time: " + Duration.ofNanos(stepTime).toMillis() + "ms", 20, 85);
        g.drawString("viewer frame time: " + Duration.ofNanos(frameTime).toMillis() + "ms", 20, 100);

        // draw border circle
        Point center = simToPixels(0, 0);
        drawCircle(g, center.x, center.y, distanceToPixels(sim.getBoundary()));

        for (SimulationBody body : sim.getBodies()) {
            Point location = simToPixels(body.getState().getPosition());

            g.setColor(body.getState().getColor());
            int radius = distanceToPixels(body.getState().getRadius());
            if (radius < 1) radius = 1;
            drawCircle(g, location.x, location.y, radius);

            if (forceVectors) {
                drawForceVector(g, body);
            }

            if (historyTrails) {
                // Swing uses Graphics2D internally, so this downcast is safe
                drawHistoryTrail((Graphics2D) g, body, colorTrails);
            }
        }

        // Draw a rectangle around the selected body, if one exists
        if (selection != null) {
            highlightBody(g, selection.getState());
        }

        // Smooth measurement by averaging with previous
        frameTime = (frameTime + (System.nanoTime() - startTime)) / 2;
    }

    private void highlightBody(Graphics g, Body body) {
        Point selected = simToPixels(body.getPosition());
        int radius = distanceToPixels(body.getRadius());
        if (radius < 1) radius = 1;
        g.setColor(Color.GREEN);
        g.drawRect(selected.x - radius, selected.y - radius, radius * 2, radius * 2);
    }

    /**
     * Draw a short red line representing the direction of the net force acting on a given body.
     *
     * @param g    the graphics context used for drawing
     * @param body the body for which to draw the force vector
     */
    private void drawForceVector(Graphics g, SimulationBody body) {
        Point bodyLocation = simToPixels(body.getState().getPosition());

        // Normalize the force vector
        double forceMagnitude = body.getState().getForce().magnitude();
        if (forceMagnitude != 0) {
            Vec2 normalized = body.getState().getForce().divide(forceMagnitude);

            // Calculate the endpoint of the vector
            int vectorLength = 20;
            int endX = bodyLocation.x + (int) (normalized.x() * vectorLength);
            int endY = bodyLocation.y + (int) (normalized.y() * vectorLength);

            // Draw the vector
            g.setColor(Color.RED);
            g.drawLine(bodyLocation.x, bodyLocation.y, endX, endY);
        }
    }

    /**
     * Draws the historical positions of the body as a series of connected lines on the given
     * graphics context.
     *
     * @param g    the graphics context used to draw the position history
     * @param body the body whose position history is to be drawn
     */
    private void drawHistoryTrail(Graphics2D g, SimulationBody body, boolean color) {
        final int SEGMENT_LENGTH = 20;
        Path2D path = new Path2D.Double();

        body.getHistory().enumerate((i, state) -> {
            Point current = simToPixels(state.getPosition());

            if (i == 0) {
                path.moveTo(current.x, current.y);
            } else {
                path.lineTo(current.x, current.y);
            }

            // Updating color + opacity for every history point is slow, so use 20 element segments
            if (i % SEGMENT_LENGTH == 0 || i == body.getHistory().size() - 1) {
                float opacity = i / (float) body.getHistory().size();
                g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, opacity));
                g.setColor(color ? state.getColor() : Color.GRAY);
                g.draw(path);
                path.reset();
                path.moveTo(current.x, current.y);
            }
        });
    }

    /**
     * Draw a circle using the given graphics context. Much more pleasant to use then the default
     * drawOval() method.
     *
     * @param g      the graphics context to use
     * @param x      the x-coordinate of the center of the circle to draw
     * @param y      the y-coordinate of the center of the circle to draw
     * @param radius the radius, in pixels, or the circle to draw
     */
    public void drawCircle(Graphics g, int x, int y, int radius) {
        g.drawOval(x - radius, y - radius, radius * 2, radius * 2);
    }

    /**
     * Updates the display to match the current state of the currently observed simulation and some
     * user interaction data.
     */
    public void update() {
        if (frame != null) {
            repaint();
        }
        if (isPanning) {
            updatePan();
        }
    }

    /**
     * Record the starting location for panning using the mouse. The amount panned exactly
     * corresponds to the distance moved by the mouse.
     */
    void startPan() {
        isPanning = true;
        panStart = new Point(pan.x, pan.y);
        panStartMouse = mouseLocationOnScreen();
    }

    /**
     * Stop panning. Usually called when the 'pan button' is released.
     */
    void endPan() {
        isPanning = false;
    }

    /**
     * Get the current location of the mouse relative to the top left of the window.
     *
     * @return the current location of the mouse.
     */
    private Point mouseLocationOnScreen() {
        Point screen = MouseInfo.getPointerInfo().getLocation();
        Point window = frame.getLocationOnScreen();
        return new Point(screen.x - window.x, screen.y - window.y);
    }

    /**
     * Update how far the viewer is panned from being centered around the origin. Called while the
     * 'pan button' is pressed (usually right mouse button).
     */
    void updatePan() {
        Point current = mouseLocationOnScreen();
        int dx = current.x - panStartMouse.x;
        int dy = current.y - panStartMouse.y;
        pan.setLocation(panStart.x + dx, panStart.y + dy);
    }

    void centerWindowOn(Vec2 position) {
        pan.x = (int) (-position.x() / scale);
        pan.y = (int) (-position.y() / scale);
    }

    public void run() {
        new java.util.Timer().scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                update();
            }
        }, 0, config.getRepaintInterval());
    }

    /**
     * Automatically step the simulation at a real-world time interval. Call stopAutoStep to stop
     * this behavior.
     *
     * @param stepDelay the amount of time to wait between simulation steps.
     */
    void autoStep(long stepDelay) {
        stopAutoStep();
        timer = new java.util.Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                long startTime = System.nanoTime();
                sim.step();

                // Smooth measurement by averaging with previous
                stepTime = (stepTime + (System.nanoTime() - startTime)) / 2;
            }
        }, 0, stepDelay);
        running = true;
    }

    /**
     * Stops the autoStep timer. If autoStep is not running, this method does nothing.
     */
    void stopAutoStep() {
        if (timer != null) {
            timer.cancel();
        }
        running = false;
    }
}
