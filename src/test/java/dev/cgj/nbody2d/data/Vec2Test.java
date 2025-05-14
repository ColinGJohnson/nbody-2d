package dev.cgj.nbody2d.data;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.*;
import static dev.cgj.nbody2d.data.Vec2.ZERO;

class Vec2Test {

    /** Threshold for floating point comparisons. */
    public static final double DELTA = 1e-9;

    @Test
    void magnitude_positiveCoordinates() {
        Vec2 vector = new Vec2(3, 4);
        assertEquals(5.0, vector.magnitude(), DELTA);
    }

    @Test
    void magnitude_negativeCoordinates() {
        Vec2 vector = new Vec2(-6, -8);
        assertEquals(10.0, vector.magnitude(), DELTA);
    }

    @Test
    void magnitude_zero() {
        assertEquals(0.0, ZERO.magnitude(), DELTA);
    }

    @ParameterizedTest
    @CsvSource({
        "3, 4, 1, 2, 2, 2",
        "5, 7, 3, 2, 2, 5",
        "1.5, 2.5, 0.5, 1.5, 1, 1",
        "-3, -4, -1, -2, -2, -2"
    })
    void subtract(double x1, double y1, double x2, double y2, double expectedX, double expectedY) {
        Vec2 a = new Vec2(x1, y1);
        Vec2 b = new Vec2(x2, y2);
        Vec2 result = a.subtract(b);
        assertEquals(expectedX, result.x(), DELTA);
        assertEquals(expectedY, result.y(), DELTA);
    }

    @Test
    void subtract_zero() {
        Vec2 vector = new Vec2(3.0, 4.0);
        Vec2 result = vector.subtract(Vec2.ZERO);
        assertEquals(3.0, result.x(), DELTA);
        assertEquals(4.0, result.y(), DELTA);
    }

    @Test
    void subtract_negativeCoordinates() {
        Vec2 a = new Vec2(-2.0, -3.0);
        Vec2 b = new Vec2(-5.0, -6.0);
        Vec2 result = a.subtract(b);
        assertEquals(3.0, result.x(), DELTA);
        assertEquals(3.0, result.y(), DELTA);
    }

    @ParameterizedTest
    @CsvSource({
        "1, 2, 3, 4, 4, 6",
        "-1, -2, -3, -4, -4, -6",
        "0, 0, 5, 5, 5, 5",
        "1.5, 2.5, 3.5, 4.5, 5, 7"
    })
    void add_withVariousInputs(double x1, double y1, double x2, double y2, double expectedX, double expectedY) {
        Vec2 a = new Vec2(x1, y1);
        Vec2 b = new Vec2(x2, y2);
        Vec2 result = a.add(b);
        assertEquals(expectedX, result.x(), DELTA);
        assertEquals(expectedY, result.y(), DELTA);
    }

    @Test
    void add_withZeroVector() {
        Vec2 vector = new Vec2(3.0, 4.0);
        Vec2 result = vector.add(Vec2.ZERO);
        assertEquals(3.0, result.x(), DELTA);
        assertEquals(4.0, result.y(), DELTA);
    }

    @Test
    void add_negativeCoordinates() {
        Vec2 a = new Vec2(-2.0, -3.0);
        Vec2 b = new Vec2(-5.0, -6.0);
        Vec2 result = a.add(b);
        assertEquals(-7.0, result.x(), DELTA);
        assertEquals(-9.0, result.y(), DELTA);
    }

    @ParameterizedTest
    @CsvSource({
        "2, 3, 2, 4, 6",
        "-1, -2, 3, -3, -6",
        "0, 0, 5, 0, 0",
        "1.5, -2.5, 2, 3.0, -5.0"
    })
    void multiply(double x, double y, double factor, double expectedX, double expectedY) {
        Vec2 vector = new Vec2(x, y);
        Vec2 result = vector.multiply(factor);
        assertEquals(expectedX, result.x(), DELTA);
        assertEquals(expectedY, result.y(), DELTA);
    }

    @Test
    void multiply_zeroFactor() {
        Vec2 vector = new Vec2(3.0, 4.0);
        Vec2 result = vector.multiply(0);
        assertEquals(ZERO.x(), result.x(), DELTA);
        assertEquals(ZERO.y(), result.y(), DELTA);
    }

    @Test
    void multiply_oneFactor() {
        Vec2 vector = new Vec2(3.0, 4.0);
        Vec2 result = vector.multiply(1);
        assertEquals(3.0, result.x(), DELTA);
        assertEquals(4.0, result.y(), DELTA);
    }
}
