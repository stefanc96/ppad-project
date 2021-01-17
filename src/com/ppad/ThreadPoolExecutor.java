package com.ppad;

import java.util.*;

public class ThreadPoolExecutor {
    private final BlockingQueue<Runnable> runnablesQueue;
    private final int corePoolSize;
    private final int maximumPoolSize;
    private final int keepAliveTime;
    private volatile Boolean isRunning = true;
    private final List<WorkerThread> threads;

    public ThreadPoolExecutor(int corePoolSize, int maximumPoolSize, int keepAliveTime, int queueSize) {
        if (corePoolSize < 0 || maximumPoolSize <= 0 || maximumPoolSize < corePoolSize || keepAliveTime < 0)
            throw new IllegalArgumentException();
        this.corePoolSize = corePoolSize;
        this.keepAliveTime = keepAliveTime;
        this.runnablesQueue = new BlockingQueue<>(queueSize);
        this.maximumPoolSize = maximumPoolSize;
        this.threads = new ArrayList<>();
        createCoreThreads();
    }

    private void createCoreThreads() {
        for (int threadIndex = 0; threadIndex < this.corePoolSize; threadIndex++) {
            WorkerThread thread = new WorkerThread("Thread " + threadIndex, this.runnablesQueue, this.keepAliveTime, ThreadType.CORE);
            thread.start();
            this.threads.add(thread);
        }
    }

    public void addWorker() {
        if (threads.size() + 1 > maximumPoolSize) {
            throw new ExecutionThreadPoolException(new Throwable("The number of threads is higher than the maximumPoolSize"));
        }
        WorkerThread thread = new WorkerThread("Thread " + threads.size(), this.runnablesQueue, this.keepAliveTime, ThreadType.TEMPORARY);
        thread.start();
        this.threads.add(thread);
    }

    public void execute(Runnable runnable) {
        if (isRunning) {
            try {
                runnablesQueue.add(runnable);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        } else {
            throw new IllegalStateException("Threadpool terminating, unable to execute runnable");
        }
    }

    public void awaitTermination() throws InterruptedException {
        while (!runnablesQueue.isEmpty()){}
        stop();
    }

    public void stop() {
        this.runnablesQueue.setRunning(false);
        isRunning = false;
        for (WorkerThread thread : threads) {
            thread.setIsActive(false);
        }
    }
}