package dev.cgj.nbody2d.simulation;

import dev.cgj.nbody2d.config.BoundaryType;
import dev.cgj.nbody2d.util.BoundedQueue;
import lombok.Getter;

import java.awt.Color;
import java.util.List;
import java.util.Objects;

/**
 * An object used to represent a body. Each body keeps track of its position, velocity, and the
 * forces acting on it.
 */
@Getter
public class Body {

    /**
     * Softening parameter (epsilon) determining the minimum distance between this body and another
     * when calculating forces to avoid infinite forces at very short distances.
     */
    public static final double EPS = 3E4;

    /**
     * Universal gravitational constant.
     */
    public static final double G = 6.673e-11;

    public BodyState state;

    private final BoundedQueue<BodyState> history = new BoundedQueue<>(20);

    private boolean active = true;

    /**
     * Body Constructor; bodies initially have 0 velocity relative to the origin and have their
     * color set to white.
     *
     * @param x the initial x-position of this body
     * @param y the initial y-position of this body
     * @param mass the mass of this body
     * @param r the radius of this body
     */
    public Body(double x, double y, double mass, double r, double vx, double vy) {
        this.state = BodyState.builder()
            .position(new Vec2(x, y))
            .velocity(new Vec2(vx, vy))
            .mass(mass)
            .radius(r)
            .color(Color.WHITE)
            .build();
    }

    /**
     * Updates the color of this body to match the current force acting on it. The color is
     * assigned relative to a given limit. e.g. force 50% of the limit results in a color 50%
     * through the range of HSB hues.
     *
     * @param limit a reference value
     */
    public void updateColor(double limit) {
        float h = 0.5f * (float)Math.pow((state.getForce().magnitude() / limit), 0.3);
        float s = 0.7f;
        float b = 1f;
        state = state.withColor((Color.getHSBColor(h, s, b)));
    }

    /**
     * Updates the forces currently acting on this body using Newtonian Gravity. Does not affect
     * position or velocity.
     *
     * @param bodies an ArrayList of other bodies whose gravity should be considered.
     */
    public void updateForces(List<Body> bodies) {
        Vec2 netForce = Vec2.ZERO;

        for (Body other : bodies) {

            // don't calculate the force due to gravity between two bodies which are the same.
            if (other == this) continue;

            // The two bodies cannot be so close that they would overlap.
            double dist = Math.max(
                state.getRadius() + other.state.getRadius(),
                state.getPosition().distanceFrom(other.getState().getPosition())
            );

            Vec2 F = other.state.getPosition().minus(state.getPosition()).dividedBy(dist)
                    .times((G * state.getMass() * other.state.getMass()) / (dist * dist + EPS * EPS));
            netForce = netForce.plus(F);
        }

        state = state.withForce(netForce);
    }

    /**
     * Updates the velocity of this body given the forces currently acting on it. Does not affect
     * position or forces.
     *
     * @param dt delta time; the length of time for which the acceleration produced by the current
     *           forces on this body should be applied
     */
    public void updateVelocity(double dt) {
        Vec2 delta = state.getForce().times(dt).dividedBy(state.getMass());
        state = state.withVelocity(state.getVelocity().plus(delta));
    }

    /**
     * Updates the position of this body given the current velocity. Does not affect forces or
     * velocity.
     *
     * @param dt delta time
     */
    public void updatePosition(double dt) {
        Vec2 delta = state.getVelocity().times(dt);
        state = state.withPosition(state.getPosition().plus(delta));
        history.add(state);
    }

    /**
     * If the body is more than {@code boundary} meters from the origin, place it on the boundary
     * and stop it.
     * 
     * @param boundary Maximum distance from the origin for this body's position.
     */
    public void applyBoundary(BoundaryType type, double boundary) {
        if (Objects.requireNonNull(type) == BoundaryType.NONE) {
            return;
        }

        double fromOrigin = this.state.getPosition().magnitude();

        if (fromOrigin > boundary) {
            if (type == BoundaryType.STICK) {
                this.active = false;
            } else if (type == BoundaryType.WRAP) {
                boundary = -boundary;
            }

            state = state.withPosition(state.getPosition().dividedBy(fromOrigin).times(boundary));

            if (type == BoundaryType.STOP) {
                state = state.withVelocity(Vec2.ZERO);
            }
        }
    }
}
