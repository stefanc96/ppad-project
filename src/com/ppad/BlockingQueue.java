package com.ppad;

import java.util.LinkedList;
import java.util.List;

public class BlockingQueue<T> {

    private final List<T> queue = new LinkedList<>();
    private final int size;
    private volatile boolean isRunning = true;

    public BlockingQueue(int queueSize) {
        this.size = queueSize;
    }

    public synchronized void add(T item)
            throws InterruptedException {
        while (queue.size() == size) {
            wait();
        }
        queue.add(item);
        if (queue.size() != 0) {
            notifyAll();
        }
    }

    public synchronized T poll()
            throws InterruptedException {
        while (queue.size() == 0 && isRunning) {
            wait();
        }
        if (queue.size() > 0) {
            notifyAll();
        }
        if(!isRunning) {
            return null;
        }

        return queue.remove(0);
    }

    public void setRunning(boolean running) {
        isRunning = running;
    }

    public synchronized boolean isEmpty() {
        if(queue.isEmpty()){
            notifyAll();
        }
        return queue.isEmpty();
    }
}