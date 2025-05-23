package dev.cgj.nbody2d.util;

import lombok.Getter;

import java.util.ArrayDeque;
import java.util.List;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.function.BiConsumer;
import java.util.function.Supplier;

public class BoundedQueue<T> {
    private final ArrayDeque<T> deque = new ArrayDeque<>();

    /**
     * Simulation updates happen on a separate thread to the viewer, so we must ensure that the
     * queue is not modified while being read for drawing.
     */
    private final ReadWriteLock lock = new ReentrantReadWriteLock();

    @Getter
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
        withReadLock(() -> {
            int i = 0;
            for (T item : deque) {
                f.accept(i++, item);
            }
            return null;
        });
    }

    public int size() {
        return withReadLock(deque::size);
    }

    public T peek() {
        return withReadLock(deque::peekLast);
    }

    public List<T> asList() {
        return withReadLock(() -> deque.stream().toList());
    }

    public List<T> asList(int limit) {
        return withReadLock(() -> deque.stream().limit(limit).toList());
    }

    private <R> R withReadLock(Supplier<R> supplier) {
        lock.readLock().lock();
        try {
            return supplier.get();
        } finally {
            lock.readLock().unlock();
        }
    }
}
