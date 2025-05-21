package dev.cgj.nbody2d.data;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class BodyTest {

    @Test
    void overlapsWith_overlappingBodies_shouldReturnTrue() {
        Body body1 = Body.builder().id("1").position(new Vec2(0, 0)).radius(5.0).build();
        Body body2 = Body.builder().id("2").position(new Vec2(3, 3)).radius(5.0).build();
        assertTrue(body1.overlapsWith(body2));
    }

    @Test
    void overlapsWith_nonOverlappingBodies_shouldReturnFalse() {
        Body body1 = Body.builder().id("1").position(new Vec2(0, 0)).radius(5.0).build();
        Body body2 = Body.builder().id("2").position(new Vec2(15, 15)).radius(5.0).build();
        assertFalse(body1.overlapsWith(body2));
    }

    @Test
    void overlapsWith_edgeCaseTouchingBodies_shouldReturnTrue() {
        Body body1 = Body.builder().id("1").position(new Vec2(0, 0)).radius(5.0).build();
        Body body2 = Body.builder().id("2").position(new Vec2(10, 0)).radius(5.0).build();
        assertTrue(body1.overlapsWith(body2));
    }

    @Test
    void overlapsWith_identicalBodies_shouldReturnTrue() {
        Body body1 = Body.builder().id("1").position(new Vec2(0, 0)).radius(5.0).build();
        Body body2 = Body.builder().id("2").position(new Vec2(0, 0)).radius(5.0).build();
        assertTrue(body1.overlapsWith(body2));
    }

    @Test
    void overlapsWith_zeroRadiusBodies_shouldReturnFalse() {
        Body body1 = Body.builder().id("1").position(new Vec2(0, 0)).radius(0.0).build();
        Body body2 = Body.builder().id("2").position(new Vec2(10, 10)).radius(0.0).build();
        assertFalse(body1.overlapsWith(body2));
    }
}
