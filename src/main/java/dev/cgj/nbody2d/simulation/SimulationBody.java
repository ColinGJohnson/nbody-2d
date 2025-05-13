package dev.cgj.nbody2d.simulation;

import dev.cgj.nbody2d.config.BoundaryType;
import dev.cgj.nbody2d.data.Body;
import dev.cgj.nbody2d.data.Vec2;
import dev.cgj.nbody2d.util.BoundedQueue;
import lombok.Getter;
import lombok.Setter;

import java.awt.Color;
import java.util.List;
import java.util.Objects;

/**
 * An object used to represent a body. Each body keeps track of its position, velocity, and the
 * forces acting on it.
 */
@Getter
public class SimulationBody {

    private final BoundedQueue<Body> history = new BoundedQueue<>(20);
    private boolean active = true;

    @Setter
    private Body state;

    /**
     * @param body Initial state for this body.
     */
    public SimulationBody(Body body) {
        this.state = body;
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
    public void updateForces(List<SimulationBody> bodies) {
        Vec2 netForce = Vec2.ZERO;

        for (SimulationBody other : bodies) {

            // don't calculate the force due to gravity between two bodies which are the same.
            if (other == this) continue;

            // The two bodies cannot be so close that they would overlap.
            double dist = Math.max(
                state.getRadius() + other.state.getRadius(),
                state.getPosition().distanceFrom(other.getState().getPosition())
            );

            Vec2 F = other.state.getPosition().subtract(state.getPosition()).divide(dist)
                    .multiply((RealTimeSimulation.G * state.getMass() * other.state.getMass()) / (dist * dist + RealTimeSimulation.EPS * RealTimeSimulation.EPS));
            netForce = netForce.add(F);
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
        Vec2 delta = state.getForce().multiply(dt).divide(state.getMass());
        state = state.withVelocity(state.getVelocity().add(delta));
    }

    /**
     * Updates the position of this body given the current velocity. Does not affect forces or
     * velocity.
     *
     * @param dt delta time
     */
    public void updatePosition(double dt) {
        Vec2 delta = state.getVelocity().multiply(dt);
        state = state.withPosition(state.getPosition().add(delta));
        history.add(state);
    }

    /**
     * Updates the current state of the body and appends the new state to the history.
     *
     * @param state The new state of the body to be set and recorded in history.
     */
    public void pushState(Body state) {
        this.state = state;
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
                active = false;
            } else if (type == BoundaryType.WRAP) {
                boundary = -boundary;
            }

            state = state.withPosition(state.getPosition().divide(fromOrigin).multiply(boundary));

            if (type == BoundaryType.STOP) {
                state = state.withVelocity(Vec2.ZERO);
            }
        }
    }
}
