package Particles;

public class GravPoint {
	private double x;
	private double y;
	private double mass;
	
	public GravPoint(double x, double y, double mass){
		this.x = x;
		this.y = y;
		this.mass = mass;
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
	
	public double getMass() {
		return mass;
	}
}
