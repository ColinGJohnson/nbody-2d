package dev.cgj.nbody2d.viewer;

import javax.swing.event.MouseInputListener;
import java.awt.Cursor;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.geom.Point2D;

/**
 * Handles input events (mouse and keyboard) for NBody2dViewer.
 */
public class InputHandler implements MouseInputListener, MouseWheelListener, KeyListener {

    private final Viewer viewer;

    public InputHandler(Viewer viewer) {
        this.viewer = viewer;
    }

    @Override
    public void mousePressed(MouseEvent e) {
        if (e.getButton() == MouseEvent.BUTTON1) {
            viewer.startPan();
            Cursor cursor = new Cursor(Cursor.MOVE_CURSOR);
            viewer.frame.setCursor(cursor);
        } else if (e.getButton() == MouseEvent.BUTTON2) {
            // TODO: Use to select a body for tracking
        }
    }

    @Override
    public void mouseReleased(MouseEvent e) {

        // BUTTON1 = left mouse button
        if (e.getButton() == MouseEvent.BUTTON1) {
            viewer.endPan();
            Cursor cursor = new Cursor(Cursor.DEFAULT_CURSOR);
            viewer.frame.setCursor(cursor);

        // BUTTON2 = middle mouse button
        } else if (e.getButton() == MouseEvent.BUTTON2) {
            viewer.setScaleToFit();
        }
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
            System.exit(0);

        } else if (e.getKeyCode() == KeyEvent.VK_SPACE) {
            if (viewer.running) {
                viewer.stopAutoStep();
            } else {
                viewer.autoStep(viewer.config.getAutoStepInterval());
            }
        } else if (e.getKeyCode() == KeyEvent.VK_UP) {
            viewer.pan.y += 20;

        } else if (e.getKeyCode() == KeyEvent.VK_DOWN) {
            viewer.pan.y -= 20;

        } else if (e.getKeyCode() == KeyEvent.VK_LEFT) {
            viewer.pan.x += 20;

        } else if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
            viewer.pan.x -= 20;

        } else if (e.getKeyCode() == KeyEvent.VK_R) {
            viewer.stopAutoStep();
            viewer.sim.resetBodies();

        } else if (e.getKeyCode() == KeyEvent.VK_F) {
            viewer.forceVectors = !viewer.forceVectors;

        } else if (e.getKeyCode() == KeyEvent.VK_C) {
            viewer.colorTrails = !viewer.colorTrails;

        } else if (e.getKeyCode() == KeyEvent.VK_T) {
            viewer.historyTrails = !viewer.historyTrails;

        } else if (e.getKeyCode() == KeyEvent.VK_F11) {
            viewer.toggleFullScreen();
        }
    }

    @Override
    public void mouseWheelMoved(MouseWheelEvent e) {

        // get the simulation coordinates under the mouse before changing the scale
        Point2D.Double before = viewer.pixelsToSim(e.getPoint());

        // modify the scale (zoom in or our with the mouse wheel)
        double rotation = e.getPreciseWheelRotation();
        double newScale = viewer.scale + viewer.scale / 10 * rotation;
        if (newScale > 0) viewer.scale = newScale;

        // update pan so that area being pointed to stays the same
        Point2D.Double after = viewer.pixelsToSim(e.getPoint());
        viewer.pan.x += viewer.distanceToPixels(after.x - before.x);
        viewer.pan.y -= viewer.distanceToPixels(after.y - before.y);
    }

    @Override
    public void mouseClicked(MouseEvent e) {}

    @Override
    public void mouseEntered(MouseEvent e) {}

    @Override
    public void mouseExited(MouseEvent e) {}

    @Override
    public void mouseDragged(MouseEvent e) {}

    @Override
    public void mouseMoved(MouseEvent e) {}

    @Override
    public void keyTyped(KeyEvent e) {}

    @Override
    public void keyReleased(KeyEvent e) {}
}
