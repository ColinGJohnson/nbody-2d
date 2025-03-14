package dev.cgj.nbody2d;

import java.awt.Color;

public class Body2dState {
    public double x;     // x-distance from the origin (in meters)
    public double y;     // y-distance from the origin (in meters)
    public double vx;    // x-velocity (in meters per second)
    public double vy;    // y-velocity (in meters per second)
    public double fx;    // x-force (in Newtons)
    public double fy;    // y-force (in Newtons)
    public double r;     // physical radius of this body (in meters)
    public double mass;  // the mass of this body (in kilograms)
    public Color color;  // the color of this body (not used in calculations)

    public Body2dState() { }

    public Body2dState(Body2dState other) {
        this.x = other.x;
        this.y = other.y;
        this.vx = other.vx;
        this.vy = other.vy;
        this.fx = other.fx;
        this.fy = other.fy;
        this.r = other.r;
        this.mass = other.mass;
        this.color = other.color;
    }
}
