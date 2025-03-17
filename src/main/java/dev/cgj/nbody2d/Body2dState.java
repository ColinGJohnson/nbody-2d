package dev.cgj.nbody2d;

import lombok.Data;

import java.awt.Color;

@Data
public class Body2dState {
    private double x;     // x-distance from the origin (in meters)
    private double y;     // y-distance from the origin (in meters)

    private double vx;    // x-velocity (in meters per second)
    private double vy;    // y-velocity (in meters per second)

    private double fx;    // x-force (in Newtons)
    private double fy;    // y-force (in Newtons)

    private double r;     // physical radius of this body (in meters)
    private double mass;  // the mass of this body (in kilograms)
    private Color color;  // the color of this body (not used in calculations)

    public Body2dState() { }

    public Body2dState(Body2dState other) {
        this.setX(other.getX());
        this.setY(other.getY());
        this.setVx(other.getVx());
        this.setVy(other.getVy());
        this.setFx(other.getFx());
        this.setFy(other.getFy());
        this.setR(other.getR());
        this.setMass(other.getMass());
        this.setColor(other.getColor());
    }
}
