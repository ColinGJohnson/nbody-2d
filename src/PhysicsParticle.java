import java.awt.Color;
import java.awt.Graphics;
import java.util.ArrayList;

public class PhysicsParticle {

	private double angle = 0;
	private double x;
	private double y;
	private double Anet;
	private double Ax = 0;
	private double Ay = 0;
	private double Vnet = 0;
	public double Vx = 0;
	public double Vy = 0; //
	private double F; // (N)
	private double m1 = 1000; // (kg)
	private double m2 = 5.972 * Math.pow(10, 24); // (kg)
	private double ticksPerSecond = 60; // t = 1s/ticksPerSecond (normally runs at 60t/s)
	private double t; // time passed (s)
	private double G = 6.673 * Math.pow(10, -11);
	private double dnet = 0; // net distance 
	private double dx = 0; // y distance 
	private double dy = 0; // x distance 1px = 1 m;
	private double simScale = Math.pow(9.5, 10); // m/pixel drawn

	private int mousePosX;
	private int mousePosY;

	private Color color;
	private int size;

	public PhysicsParticle(int x, int y, double Vx, double Vy, int size, double mass, Color c) {
		this.setX(x);
		this.setY(y);
		this.Vx = Vx;
		this.Vy = Vy;
		this.size = size;
		this.color = c;
	}
	// F = (G * m1 * m2) / d^2

	// F = Ma
	// a = F/M

	public void update(int mouseX, int mouseY, ArrayList<GravPoint> points, boolean updateMouse) {
		
		// update for gravPoints
		for (int j = 0; j < points.size(); j++) {
			double posX = points.get(j).getX();
			double posY = points.get(j).getY();
			double pointMass = points.get(j).getMass();
			
			// particle to mouse x and y distance
			dx = (posX - x);
			dy = (posY - y);

			// extra info
			angle = Math.abs(Math.atan(dy / dx));
			dnet = Math.sqrt(dy * dy + dx * dx);
			Vnet = Math.sqrt(Vy * Vy + Vx * Vx);

			// acceleration
			F = (G * m1 * pointMass) / Math.pow(dnet, 2);
			Anet = (F / m1) / ticksPerSecond;

			Ax = Anet * Math.cos(angle);
			Ay = Anet * Math.sin(angle);

			// apply acceleration in correct direction
			if (posX > x && Ax < 0) {
				Ax = -Ax;
			}
			if (posX < x && Ax > 0) {
				Ax = -Ax;
			}
			if (posY > y && Ay < 0) {
				Ay = -Ay;
			}
			if (posY < y && Ay > 0) {
				Ay = -Ay;
			}	
			
			// apply acceleration
			Vx += Ax;
			Vy += Ay;
		}
		
		// update mouse if in window
		if (mouseX > 0 && mouseY > 0 && updateMouse) {

			// particle to mouse x and y distance
			dx = mouseX - x;
			dy = (mouseY - y);

			// extra info
			angle = Math.abs(Math.atan(dy / dx));
			dnet = Math.sqrt(dy * dy + dx * dx);
			Vnet = Math.sqrt(Vy * Vy + Vx * Vx);

			// acceleration
			F = (G * m1 * m2) / Math.pow(dnet, 2);
			Anet = (F / m1) / ticksPerSecond;

			Ax = Anet * Math.cos(angle);
			Ay = Anet * Math.sin(angle);

			// apply acceleration in correct direction
			if (mouseX > x && Ax < 0) {
				Ax = -Ax;
			}
			if (mouseX < x && Ax > 0) {
				Ax = -Ax;
			}

			if (mouseY > y && Ay < 0) {
				Ay = -Ay;
			}

			if (mouseY < y && Ay > 0) {
				Ay = -Ay;
			}	
			
			// apply acceleration
			Vx += Ax;
			Vy += Ay;
		}
		
		double speedLimit = Math.pow(10, 11);
		
		if(Vx > speedLimit){
			Vx = speedLimit;
		}
		if(Vy > speedLimit){
			Vy = speedLimit;
		}
		
		if(Vx < -1 * speedLimit){
			Vx = -1 * speedLimit;
		}
		if(Vy < -1 * speedLimit){
			Vy = -1 * speedLimit;
		}
		
		// apply velocity
		x += Vx / simScale;
		y += Vy / simScale;
			
		// dynamic color
		float h = (float) (1f - 1f * (Vnet/5e10));
		float s = (float) (0.1f + 0.5f * (Vnet/5e10));
		float b = (float) (0.3f + 0.5f * (Vnet/5e10));
		s = 0.6f;  
		
		color = Color.getHSBColor(h, s, b);
		
	}

	public void draw(Graphics g) {
		g.setColor(color);
		g.fillRect((int) (Math.round(x)), (int) Math.round(y), size, size);
	}

	public double getX() {
		return x;
	}

	public void setX(double x) {
		this.x = x;
	}

	public double getY() {
		return y;
	}

	public void setY(double y) {
		this.y = y;
	}
} // PhysicsParticle
