package dev.cgj.nbody2d;

import java.util.ArrayDeque;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.function.BiConsumer;

public class BoundedQueue<T> {
    private final ArrayDeque<T> deque = new ArrayDeque<>();

    /**
     * Simulation updates happen on a separate thread to the viewer, so we must ensure that the
     * queue is not modified while being read for drawing.
     */
    private final ReadWriteLock lock = new ReentrantReadWriteLock();

    private final int maxSize;

    public BoundedQueue(int maxSize) {
        this.maxSize = maxSize;
    }

    public void add(T item) {
        lock.writeLock().lock();
        try {
            if (deque.size() == maxSize) {
                deque.removeFirst();
            }
            deque.addLast(item);
        } finally {
            lock.writeLock().unlock();
        }
    }

    public void enumerate(BiConsumer<Integer, T> f) {
        lock.readLock().lock();
        try {
            int i = 0;
            for (T item : deque) {
                f.accept(i++, item);
            }
        } finally {
            lock.readLock().unlock();
        }
    }

    public int size() {
        lock.readLock().lock();
        try {
            return deque.size();
        } finally {
            lock.readLock().unlock();
        }
    }
}
