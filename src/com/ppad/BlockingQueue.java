package com.ppad;

import java.util.LinkedList;
import java.util.List;

public class BlockingQueue<T> {

    private final List<T> queue = new LinkedList<>();
    private int size = 10;

    public BlockingQueue(int queueSize) {
        this.size = queueSize;
    }

    public synchronized void add(T item)
            throws InterruptedException {
        while (queue.size() == size) {
            wait();
        }
        queue.add(item);
        if (queue.size() == 1) {
            notifyAll();
        }
    }

    public synchronized T poll()
            throws InterruptedException {
        while (queue.size() == 0) {
            wait();
        }
        if (queue.size() == size) {
            notifyAll();
        }

        return queue.remove(0);
    }

    public void clear() {
        queue.clear();
    }

    public synchronized boolean isEmpty() {
        if(queue.isEmpty())
        {
            notifyAll();
        }
        return queue.isEmpty();
    }
}