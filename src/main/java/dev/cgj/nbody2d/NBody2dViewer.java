package dev.cgj.nbody2d;

import dev.cgj.nbody2d.simulation.Body;
import dev.cgj.nbody2d.simulation.Simulation;
import dev.cgj.nbody2d.config.ViewerConfig;
import dev.cgj.nbody2d.simulation.Vec2;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.event.MouseInputListener;
import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;
import java.time.Duration;
import java.util.TimerTask;

/**
 * Application to display and manipulates a 2-dimensional n-body simulation (NBody2d). A window
 * will be created immediately upon instantiation.
 */
public class NBody2dViewer extends JPanel implements MouseInputListener, MouseWheelListener, KeyListener {

    private final ViewerConfig config;

    private long frameTime;             // how long it took to draw the last frame, in nanoseconds
    private final Simulation sim;       // the simulation being displayed
    private JFrame frame;               // the frame that the simulation is displayed in
    private boolean fullScreen = false; // is the viewer full screen currently?
    private double scale;               // simulation meters per on-screen pixel

    private boolean historyTrails;      // should trails be drawn?
    private boolean colorTrails;        // should trails be colored?
    private boolean forceVectors;       // should force vectors be rendered?
    private boolean isPanning = false;  // is the user currently panning? (right mouse button)
    private Point panStartMouse;        // mouse position at start of pan
    private Point panStart;             // pan position at start of pan
    private final Point pan;            // the current x and y distance panned from the origin

    /**
     * NBody2dViewer Constructor. Creates and configures display panel.
     *
     * @param config configuration for the viewer
     * @param sim the simulation to display
     */
    public NBody2dViewer(ViewerConfig config, Simulation sim) {
        super(true);

        this.config = config;
        this.sim = sim;
        this.pan = new Point(0, 0);

        // add mouse and keyboard event listeners to this component
        setFocusable(true);
        addMouseListener(this);
        addMouseWheelListener(this);
        addKeyListener(this);

        // create a new window on which to display the simulation
        SwingUtilities.invokeLater(() -> {
            createAndShowGUI(false);

            // set initial scale and pan now that the screen size is known
            setScaleToFit();
        });
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
     * Creates and configures a new JFrame containing a single {@link NBody2dViewer}.
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
        double boundary = sim.getConfig().getBoundary();
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
        return simToPixels(position.getX(), position.getY());
    }

    /**
     * Converts a pixel on the screen (relative to the top left of the window) to the
     * simulation's coordinate system.
     *
     * @param pixelCoordinate the coordinate on the screen.
     * @return A double point containing the coordinates in the simulation.
     */
    private Point2D.Double pixelsToSim(Point pixelCoordinate) {
        Point screenCenter = getScreenCenter();

        double simX = (pixelCoordinate.x - screenCenter.x - pan.x) * scale;
        double simY = (screenCenter.y - pixelCoordinate.y + pan.y) * scale;

        return new Point2D.Double(simX, simY);
    }

    /**
     * Converts a length measurement in the simulation to pixels using scale, for visualization
     *
     * @param distance the distance, in meters, from the simulation
     * @return a distance in pixels
     */
    private int distanceToPixels(double distance) {
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

        // dark background
        g.setColor(new Color(30, 30, 30));
        g.fillRect(0, 0, this.getWidth(), this.getHeight());

        // draw debug info
        g.setColor(Color.WHITE);
        g.drawString("tracked particles: " + sim.getBodies().size(), 20, 40);
        g.drawString(String.format("scale: %.2e meters / pixel", scale), 20, 55);
        g.drawString("sim elapsed time: " + secondsToString(sim.getTimeElapsed()), 20, 70);
        g.drawString("sim step time: " + Duration.ofNanos(sim.getStepTime()).toMillis() + "ms", 20, 85);
        g.drawString("viewer frame time: " + Duration.ofNanos(frameTime).toMillis() + "ms", 20, 100);

        // draw border circle
        Point center = simToPixels(0, 0);
        drawCircle(g, center.x, center.y, distanceToPixels(sim.getConfig().getBoundary()));

        for (Body body : sim.getBodies()) {
            Point location = simToPixels(body.state.getPosition());

            g.setColor(body.state.getColor());
            int radius = distanceToPixels(body.state.getRadius());
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

        // Smooth measurement by averaging with previous
        frameTime = (frameTime + (System.nanoTime() - startTime)) / 2;
    }

    /**
     * Draw a short red line representing the direction of the net force acting on a given body.
     *
     * @param g    the graphics context used for drawing
     * @param body the body for which to draw the force vector
     */
    private void drawForceVector(Graphics g, Body body) {
        Point bodyLocation = simToPixels(body.state.getPosition());;

        // Normalize the force vector
        double forceMagnitude = body.state.getForce().magnitude();
        if (forceMagnitude != 0) {
            Vec2 normalized = body.state.getForce().dividedBy(forceMagnitude);

            // Calculate the endpoint of the vector
            int vectorLength = 20;
            int endX = bodyLocation.x + (int) (normalized.getX() * vectorLength);
            int endY = bodyLocation.y + (int) (normalized.getY() * vectorLength);

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
    private void drawHistoryTrail(Graphics2D g, Body body, boolean color) {
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
     * Invoked when the mouse button has been clicked (pressed and released) on a component.
     *
     * @param e the event to be processed
     */
    @Override
    public void mouseClicked(MouseEvent e) {

    }

    /**
     * Invoked when a mouse button has been pressed on a component.
     *
     * @param e the event to be processed
     */
    @Override
    public void mousePressed(MouseEvent e) {

        // TODO: use one of these to "select" a body, possibly multiple, for tracking
        // TODO: treat the mouse cursor as a strong source of gravity when pressed
        if (e.getButton() == MouseEvent.BUTTON1) {
            startPan();
            Cursor cursor = new Cursor(Cursor.MOVE_CURSOR);
            frame.setCursor(cursor);

        } else if (e.getButton() == MouseEvent.BUTTON2) {

        } else if (e.getButton() == MouseEvent.BUTTON3) {

        }
    }

    /**
     * Invoked when a mouse button has been released on a component.
     *
     * @param e the event to be processed
     */
    @Override
    public void mouseReleased(MouseEvent e) {

        // left mouse button
        if (e.getButton() == MouseEvent.BUTTON1) {
            endPan();
            Cursor cursor = new Cursor(Cursor.DEFAULT_CURSOR);
            frame.setCursor(cursor);

            // middle mouse button
        } else if (e.getButton() == MouseEvent.BUTTON2) {
            setScaleToFit();

            // right mouse button
        } else if (e.getButton() == MouseEvent.BUTTON3) {

        }
    }

    /**
     * Record the starting location for panning using the mouse. The amount panned exactly
     * corresponds to the distance moved by the mouse.
     */
    private void startPan() {
        isPanning = true;
        panStart = new Point(pan.x, pan.y);
        panStartMouse = mouseLocationOnScreen();
    }

    /**
     * Stop panning. Usually called when the 'pan button' is released.
     */
    private void endPan() {
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
    private void updatePan() {
        Point current = mouseLocationOnScreen();
        int dx = current.x - panStartMouse.x;
        int dy = current.y - panStartMouse.y;
        pan.setLocation(panStart.x + dx, panStart.y + dy);
    }

    /**
     * Invoked when the mouse enters a component.
     *
     * @param e the event to be processed
     */
    @Override
    public void mouseEntered(MouseEvent e) {
    }

    /**
     * Invoked when the mouse exits a component.
     *
     * @param e the event to be processed
     */
    @Override
    public void mouseExited(MouseEvent e) {
    }

    /**
     * Invoked when a mouse button is pressed on a component and then dragged. {@code MOUSE_DRAGGED}
     * events will continue to be delivered to the component where the drag originated until the
     * mouse button is released (regardless of whether the mouse position is within the bounds of
     * the component). <p> Due to platform-dependent Drag&amp;Drop implementations, {@code
     * MOUSE_DRAGGED} events may not be delivered during a native Drag&amp;Drop operation.
     *
     * @param e the event to be processed
     */
    @Override
    public void mouseDragged(MouseEvent e) {
    }

    /**
     * Invoked when the mouse cursor has been moved onto a component but no buttons have been pushed.
     *
     * @param e the event to be processed
     */
    @Override
    public void mouseMoved(MouseEvent e) {
    }

    /**
     * Invoked when a key has been typed. See the class description for {@link KeyEvent} for a
     * definition of a key typed event.
     *
     * @param e the event to be processed
     */
    @Override
    public void keyTyped(KeyEvent e) {
    }


    /**
     * Handles key press events on the viewer. Defines custom behavior based on the key pressed.
     * <ul>
     *   <li>ESCAPE: Exit the application.</li>
     *   <li>SPACE: Toggle between starting and stopping the simulation's auto-step mode.</li>
     *   <li>Arrow keys: Pan the simulation view in the respective direction.</li>
     *   <li>R: Randomize the positions of the simulation bodies within half the boundary.</li>
     *   <li>F11: Toggle between full-screen and windowed mode.</li>
     * </ul>
     *
     * @param e the {@link KeyEvent} representing the key press event.
     */
    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
            System.exit(0);

        } else if (e.getKeyCode() == KeyEvent.VK_SPACE) {
            if (sim.isRunning()) {
                sim.stopAutoStep();
            } else {
                sim.autoStep(config.getAutoStepInterval());
            }

        } else if (e.getKeyCode() == KeyEvent.VK_DELETE) {

        } else if (e.getKeyCode() == KeyEvent.VK_UP) {
            pan.y += 20;

        } else if (e.getKeyCode() == KeyEvent.VK_DOWN) {
            pan.y -= 20;

        } else if (e.getKeyCode() == KeyEvent.VK_LEFT) {
            pan.x += 20;

        } else if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
            pan.x -= 20;

        } else if (e.getKeyCode() == KeyEvent.VK_R) {
            sim.stopAutoStep();
            sim.resetBodies();

        } else if (e.getKeyCode() == KeyEvent.VK_F) {
            forceVectors = !forceVectors;

        } else if (e.getKeyCode() == KeyEvent.VK_C) {
            colorTrails = !colorTrails;

        } else if (e.getKeyCode() == KeyEvent.VK_T) {
            historyTrails = !historyTrails;

        } else if (e.getKeyCode() == KeyEvent.VK_F11) {
            toggleFullScreen();
        }
    }

    /**
     * Invoked when a key has been released. See the class description for {@link KeyEvent} for a
     * definition of a key released event.
     *
     * @param e the event to be processed
     */
    @Override
    public void keyReleased(KeyEvent e) {
    }

    /**
     * Invoked when the mouse wheel is rotated.
     *
     * @param e the event to be processed
     * @see MouseWheelEvent
     */
    @Override
    public void mouseWheelMoved(MouseWheelEvent e) {

        // get the simulation coordinates under the mouse before changing the scale
        Point2D.Double before = pixelsToSim(e.getPoint());

        // modify the scale (zoom in or our with the mouse wheel)
        double rotation = e.getPreciseWheelRotation();
        double newScale = scale + scale / 10 * rotation;
        if (newScale > 0) scale = newScale;

        // update pan so that area being pointed to stays the same
        Point2D.Double after = pixelsToSim(e.getPoint());
        pan.x += distanceToPixels(after.x - before.x);
        pan.y -= distanceToPixels(after.y - before.y);
    }

    /**
     * TODO: this method might be broken
     * Center the window around a certain location in the simulation.
     *
     * @param x x-coordinate of the point to center the window around.
     * @param y y-coordinate of the point to center the window around.
     */
    private void centerWindowOn(double x, double y) {
        Point center = getScreenCenter();
        Point newCenter = simToPixels(x, y);
        pan.x += newCenter.x - center.x;
        pan.y -= newCenter.y - center.y;
    }
}
