package com.ppad;

import java.util.*;

public class ThreadPoolExecutor {
    private final BlockingQueue<Runnable> runnablesQueue;
    private final int corePoolSize;
    private final int maximumPoolSize;
    private final int keepAliveTime;
    private volatile Boolean execute;
    private final List<WorkerThread> threads;

    public ThreadPoolExecutor(int corePoolSize, int maximumPoolSize, int keepAliveTime, int queueSize) {
        if (corePoolSize < 0 || maximumPoolSize <= 0 || maximumPoolSize < corePoolSize || keepAliveTime < 0)
            throw new IllegalArgumentException();
        this.corePoolSize = corePoolSize;
        this.keepAliveTime = keepAliveTime;
        this.runnablesQueue = new BlockingQueue<>(queueSize);
        this.maximumPoolSize = maximumPoolSize;
        this.execute = true;
        this.threads = new ArrayList<>();
        createThreads();
    }

    private void createThreads() {
        for (int threadIndex = 0; threadIndex <= this.corePoolSize; threadIndex++) {
            WorkerThread thread = new WorkerThread("Thread" + threadIndex, this.execute, this.runnablesQueue, this.keepAliveTime);
            thread.start();
            this.threads.add(thread);
        }
    }

    public void addWorker() {
        if (threads.size() + 1 > maximumPoolSize) {
            throw new ExecutionThreadPoolException(new Throwable("The number of threads is higher than the maximumCorePoolSize"));
        }
        WorkerThread thread = new WorkerThread("Thread" + threads.size() + 1, this.execute, this.runnablesQueue, this.keepAliveTime);
        thread.start();
        this.threads.add(thread);
    }

    public void execute(Runnable runnable) {
        if (this.execute) {
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
        while (true) {
            boolean flag = true;
            for (Thread thread : threads) {
                if (thread.isAlive()) {
                    flag = false;
                    break;
                }
            }
            if (flag) {
                terminate();
                return;
            }
            try {
                Thread.sleep(1);
            } catch (InterruptedException e) {
                throw e;
            }
        }
    }

    public void terminate() {
        runnablesQueue.clear();
        stop();
    }

    public void stop() {
        execute = false;
    }
}