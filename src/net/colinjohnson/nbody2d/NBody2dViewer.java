package net.colinjohnson.nbody2d;

import javax.swing.*;
import javax.swing.event.MouseInputListener;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Point2D;

/**
 * Application to display and manipulates a 2-dimensional n-body simulation (nbody2d.NBody2d). A
 * window will be created immediately upon instantiation.
 *
 * @Author Colin Johnson
 */
public class NBody2dViewer extends JPanel implements MouseInputListener, MouseWheelListener, KeyListener {

    private NBody2d sim;                // the simulation being displayed
    private JFrame frame;               // the frame that the simulation is displayed in
    private boolean fullScreen = false; // is the viewer full screen currently?
    private double scale;               // simulation meters per on-screen pixel
    private boolean ready = false;      // is the viewer ready to display graphics?

    /**
     * NBody2dViewer Constructor. Creates and configures display panel.
     *
     * @param sim the simulation to display
     */
    public NBody2dViewer(NBody2d sim) {
        super(true);
        this.sim = sim;

        // add mouse and keyboard event listeners to this component
        setFocusable(true);
        addMouseListener(this);
        addMouseWheelListener(this);
        addKeyListener(this);

        // create a new window on which to display the simulation
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                createAndShowGUI(false);
            }
        });

        // set initial scale s.t. the whole simulated area is on screen
        scale = 1;
    }

    /**
     * Creates and configures a new JFrame containing a single nbody2d.NBody2dViewer.
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
        ready = true;
    }

    /**
     * Enables or disables full-screen mode. Questionable behavior on systems with multiple monitors.
     */
    public void toggleFullScreen() {
        ready = false;
        frame.setVisible(false);
        frame.remove(this);
        frame.dispose();
        fullScreen = !fullScreen;
        createAndShowGUI(fullScreen);
        frame.setVisible(true);
    }

    /**
     * Converts an x or y coordinate from the simulation to pixels using scale, for visualization.
     *
     * @param simCoordinate the coordinate in the simulation
     * @return a Point containing the x and y distance from the top left of the window at which
     * the simulation coordinate should be displayed.
     */
    private Point pointToPixels(Point2D.Double simCoordinate) {

        Point origin = pixelOrigin();

        int pixelX = origin.x + (int)(simCoordinate.getX() / scale);
        int pixelY = origin.y + (int)(simCoordinate.getY() / scale);

        return new Point(pixelX, pixelY);
    }

    /**
     * Converts a length measurement in the simulation to pixels using scale, for visualization
     *
     * @param distance the distance, in meters, from the simulation
     * @return a distance in pixels
     */
    private int toPixels(double distance) {
        Long pixelDistance = Math.round(distance / scale);

        if (Math.abs(pixelDistance) > Integer.MAX_VALUE) {
            System.err.println("Warning: inaccurate conversion of long pixel distance to integer");
        }

        return pixelDistance.intValue();
    }

    /**
     * Overrides the custom JComponent paint method to display custom graphics.
     *
     * @param g the graphics context to paint with
     */
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        // dark background
        g.setColor(new Color(30, 30, 30));
        g.fillRect(0, 0, this.getWidth(), this.getHeight());

        // draw debug info
        g.setColor(Color.WHITE);
        g.drawString("tracking " + sim.getN() + " particles", 20, 40);

        // draw border circle
        Point origin = pixelOrigin();
        drawCircle(g, (int)origin.getX(), (int)origin.getY(), (int)(sim.getBoundary() / scale));

        for (Body2d body : sim.getBodies()) {
            Point drawLocation = pointToPixels(new Point2D.Double(body.x, body.y));
            drawCircle(g, (int)drawLocation.getX(), (int)drawLocation.getY(), 1);
        }
    }

    /**
     * Draw a circle using the given graphics context. Much more pleasant to use then the default
     * drawOval() method.
     *
     * @param g the graphics context to use
     * @param x the x-coordinate of the center of the circle to draw
     * @param y the y-coordinate of the center of the circle to draw
     * @param radius the radius, in pixels, or the circle to draw
     */
    public void drawCircle(Graphics g, int x, int y, int radius) {
        g.drawOval(x - radius, y - radius, radius * 2, radius * 2);
    }

    /**
     * Get the location, in pixels from the top-left of the window, where the origin of the
     * simulation should be drawn.
     */
    private Point pixelOrigin() {
        //TODO: this is not the final logic (will depend on translation of the view area)
        return new Point(this.getWidth()/2, this.getHeight()/2);
    }

    private Point windowCenter() {
        return new Point();
    }

    /**
     * Updates the display to match the current state of the currently observed simulation.
     */
    public void update() {
        repaint();
    }

    /**
     * Invoked when the mouse button has been clicked (pressed and released) on a component.
     *
     * @param e the event to be processed
     */
    @Override
    public void mouseClicked(MouseEvent e) {
        Point clickLocation = e.getPoint();
        System.out.println("Mouse clicked at " + clickLocation.toString());
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
            System.out.println("button 1");

        } else if (e.getButton() == MouseEvent.BUTTON2) {
            System.out.println("button 2");

        } else if (e.getButton() == MouseEvent.BUTTON3){
            System.out.println("button 3");
            Cursor cursor = new Cursor(Cursor.MOVE_CURSOR);
            frame.setCursor(cursor);
        }
    }

    /**
     * Invoked when a mouse button has been released on a component.
     *
     * @param e the event to be processed
     */
    @Override
    public void mouseReleased(MouseEvent e) {

        if (e.getButton() == MouseEvent.BUTTON1) {
            System.out.println("button 1");

        } else if (e.getButton() == MouseEvent.BUTTON2) {
            System.out.println("button 2");

        } else if (e.getButton() == MouseEvent.BUTTON3){
            System.out.println("button 3");
            Cursor cursor = new Cursor(Cursor.DEFAULT_CURSOR);
            frame.setCursor(cursor);
        }
    }

    /**
     * Invoked when the mouse enters a component.
     *
     * @param e the event to be processed
     */
    @Override
    public void mouseEntered(MouseEvent e) {}

    /**
     * Invoked when the mouse exits a component.
     *
     * @param e the event to be processed
     */
    @Override
    public void mouseExited(MouseEvent e) {}

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
        System.out.println("mouse drag event");
    }

    /**
     * Invoked when the mouse cursor has been moved onto a component but no buttons have been pushed.
     *
     * @param e the event to be processed
     */
    @Override
    public void mouseMoved(MouseEvent e) {}

    /**
     * Invoked when a key has been typed. See the class description for {@link KeyEvent} for a
     * definition of a key typed event.
     *
     * @param e the event to be processed
     */
    @Override
    public void keyTyped(KeyEvent e) {}

    /**
     * Invoked when a key has been pressed. See the class description for {@link KeyEvent} for a
     * of a key pressed event.
     *
     * @param e the event to be processed
     */
    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
            System.out.println("'escape' pressed - exiting...");
            System.exit(0);

        } else if (e.getKeyCode() == KeyEvent.VK_ENTER) {
            System.out.println("'enter' pressed");

        } else if (e.getKeyCode() == KeyEvent.VK_SPACE) {
            System.out.println("'space' pressed");

        } else if (e.getKeyCode() == KeyEvent.VK_DELETE) {
            System.out.println("'delete' pressed");

        } else if (e.getKeyCode() == KeyEvent.VK_R) {
            System.out.println("'R' pressed");

        } else if (e.getKeyCode() == KeyEvent.VK_F11) {
            System.out.println("'f11' pressed");
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
    public void keyReleased(KeyEvent e) {}

    /**
     * Invoked when the mouse wheel is rotated.
     *
     * @param e the event to be processed
     * @see MouseWheelEvent
     */
    @Override
    public void mouseWheelMoved(MouseWheelEvent e) {
        double rotation = e.getPreciseWheelRotation();
        scale += rotation * 0.1;
        System.out.println("mouse wheel event: " + rotation + ", new scale: " + scale);
    }

    public boolean isReady() {
        return ready;
    }

    public void setReady(boolean ready) {
        this.ready = ready;
    }
}
