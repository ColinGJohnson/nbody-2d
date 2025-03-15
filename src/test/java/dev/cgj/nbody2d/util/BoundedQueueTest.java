package dev.cgj.nbody2d.util;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class BoundedQueueTest {

    @Test
    void
    add() {
        BoundedQueue<Integer> queue = new BoundedQueue<>(2);
        assertEquals(0, queue.size());
        queue.add(1);
        assertEquals(1, queue.size());
        queue.add(2);
        assertEquals(2, queue.size());
        queue.add(2);
    }

    @Test
    void enumerate() {
        BoundedQueue<Integer> queue = new BoundedQueue<>(100);
        for (int i = 0; i < 100; i++) {
            queue.add(i);
        }
        queue.enumerate(Assertions::assertEquals);
    }
}
