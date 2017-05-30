package Particles;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.Timer;

public class particleDisplayPanel extends JPanel implements MouseMotionListener,
		ActionListener, KeyListener, MouseListener {
	public long startTime = System.currentTimeMillis();
	// public Dimension screenSize =
	// Toolkit.getDefaultToolkit().getScreenSize();
	// public double width = screenSize.getWidth();
	// public double height = screenSize.getHeight();
	public double width = 1280;
	public double height = 720;
	public int mouseX;
	public int mouseY;
	public boolean updateMouse = false;
	public boolean mouseLeftPressed = true;
	public boolean mouseRightPressed = true;
	public int tps = 60;
	double fps = 0;
	private boolean animationRunning = true;
	private int numParticles = 0;
	long lastTime = System.currentTimeMillis();
	public boolean pause = false;
	private ArrayList<PhysicsParticle> particles;
	private ArrayList<GravPoint> points;

	// construct a AnimationDisplayPanel
	public particleDisplayPanel(int numParticles) {
		super(new FlowLayout(), true);
		this.numParticles = numParticles;

		// listen to key presses and mouse stuff
		setFocusable(true);
		addMouseMotionListener(this);
		addKeyListener(this);
		addMouseListener(this);

		// make graphics faster
		setDoubleBuffered(true);

		// call Animation loop() correct rate
		Timer timer = new Timer(1000 / tps, this);
		timer.start();

		// create gravity points
		points = new ArrayList<GravPoint>();

		// create particles
		particles = new ArrayList<PhysicsParticle>();
		for (int i = 0; i < numParticles; i++) {
			int xMin = (int) ((width / 2) - 150);
			int xMax = (int) ((width / 2) + 150);
			int yMin = (int) ((height / 2) - 150);
			int yMax = (int) ((height / 2) + 150);

			particles.add(new PhysicsParticle(
					ThreadLocalRandom.current().nextInt(xMin, xMax),
					ThreadLocalRandom.current().nextInt(yMin, yMax), 0, 0, 1,
					10, Color.black));
		} // for
	} // particleDisplayPanel

	public void actionPerformed(ActionEvent e) {
		AnimationLoop();
	} // actionPerformed

	public void AnimationLoop() {
		if (animationRunning) {
			fps = 1000.0 / (lastTime - (lastTime = System.currentTimeMillis()));

			// update logic if not paused
			if (!pause) {
				update();
			}

			// repaint JPanel
			repaint();
		} // if
	} // AnimationLoop

	public void update() {

		// update particles
		for (int i = 0; i < particles.size(); i++) {
			particles.get(i).update(mouseX, mouseY, points, updateMouse);
		}
	}

	// paint the game screen
	public void paintComponent(Graphics g) {
		super.paintComponent(g);

		// dark background
		g.setColor(new Color(30, 30, 30));
		g.fillRect(0, 0, this.getWidth(), this.getHeight());

		// draw particles
		for (int i = 0; i < particles.size(); i++) {
			particles.get(i).draw(g);
		} // for

		// draw debug info
		g.setColor(Color.WHITE);
		g.drawString("tracking " + particles.size() + " particles", 20, 40);

		int numOnScreen = 0;

		for (int i = 0; i < particles.size(); i++) {
			if (particles.get(i).getX() > 0 && particles.get(i).getX() < width
					&& particles.get(i).getY() > 0
					&& particles.get(i).getY() < height) {
				numOnScreen++;
			}
		}

		g.drawString(numOnScreen + " particles on screen", 20, 60);
		g.drawString(points.size() + " acting masses", 20, 80);
		g.drawString(Math.round(Math.abs(fps)) + " fps", 20, 100);

		for (int i = 0; i < points.size(); i++) {
			g.fillOval((int) points.get(i).getX(), (int) points.get(i).getY(),
					8, 8);
		}
	} // paintComponent

	@Override
	public void mouseMoved(MouseEvent e) {
		mouseX = e.getX();
		mouseY = e.getY();
	}

	@Override
	public void mouseDragged(MouseEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void keyPressed(KeyEvent e) {
		// if escape is pressed, end program
		if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
			System.exit(0);
		} // if escape pressed

		if (e.getKeyCode() == KeyEvent.VK_ENTER) {
			for (int i = 0; i < particles.size(); i++) {
				particles.get(i).Vx = 0;
				particles.get(i).Vy = 0;
				System.out.println(i);
			}

		} // if enter key pressed

		if (e.getKeyCode() == KeyEvent.VK_SPACE) {
			pause = !pause;
		} // if enter key pressed

		if (e.getKeyCode() == KeyEvent.VK_DELETE) {
			points.clear();
		} // if enter key pressed

		if (e.getKeyCode() == KeyEvent.VK_DELETE) {
			points.clear();
		} // if enter key pressed

		if (e.getKeyCode() == KeyEvent.VK_R) {
			particles.clear();

			for (int i = 0; i < numParticles; i++) {
				int xMin = (int) ((width / 2) - 150);
				int xMax = (int) ((width / 2) + 150);
				int yMin = (int) ((height / 2) - 150);
				int yMax = (int) ((height / 2) + 150);

				particles.add(new PhysicsParticle(
						ThreadLocalRandom.current().nextInt(xMin, xMax),
						ThreadLocalRandom.current().nextInt(yMin, yMax), 0, 0,
						1, 10, Color.black));
			} // for
		} // if enter key pressed
	}

	@Override
	public void keyReleased(KeyEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void keyTyped(KeyEvent e) {

	}

	@Override
	public void mouseClicked(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mousePressed(MouseEvent e) {
		if (SwingUtilities.isLeftMouseButton(e)) {
			updateMouse = !updateMouse;
		}

		if (SwingUtilities.isRightMouseButton(e)) {
			points.add(
					new GravPoint(mouseX, mouseY, (5.972 * Math.pow(10, 24))));
		}
	}

	@Override
	public void mouseReleased(MouseEvent e) {

	}
}
