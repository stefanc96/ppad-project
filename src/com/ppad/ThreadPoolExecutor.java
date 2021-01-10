package com.ppad;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class ThreadPoolExecutor {
    private final Queue<Runnable> runnablesQueue;
    private final int corePoolSize;
    private final int maximumPoolSize;
    private final int keepAliveTime;
    private final int queueSize;
    private volatile Boolean execute;
    private final List<ExecutionThread> threads;

    public ThreadPoolExecutor(int corePoolSize, int maximumPoolSize, int keepAliveTime, int queueSize) {
        if (corePoolSize < 0 ||
                maximumPoolSize <= 0 ||
                maximumPoolSize < corePoolSize ||
                keepAliveTime < 0)
            throw new IllegalArgumentException();
        this.corePoolSize = corePoolSize;
        this.keepAliveTime = keepAliveTime;
        this.runnablesQueue = new LinkedList<>();
        this.queueSize = queueSize;
        this.maximumPoolSize = maximumPoolSize;
        this.execute = true;
        this.threads = new ArrayList<>();
        createThreads();
    }

    private void createThreads() {
        for (int threadIndex = 0; threadIndex <= this.corePoolSize; threadIndex++) {
            ExecutionThread thread = new ExecutionThread("Thread" + threadIndex, this.execute, this.runnablesQueue, this.keepAliveTime);
            thread.start();
            this.threads.add(thread);
        }
    }

    public void addWorker() {
        if (threads.size() + 1 > maximumPoolSize) {
            throw new ExecutionThreadPoolException(new Throwable("The number of threads is higher than the maximumCorePoolSize"));
        }
        ExecutionThread thread = new ExecutionThread("Thread" + threads.size() + 1, this.execute, this.runnablesQueue, this.keepAliveTime);
        thread.start();
        this.threads.add(thread);
    }

    public void execute(Runnable runnable) {
        if (this.execute) {
            if (runnablesQueue.size() + 1 <= queueSize) runnablesQueue.add(runnable);
        } else {
            throw new IllegalStateException("Threadpool terminating, unable to execute runnable");
        }
    }

    public void awaitTermination() throws ExecutionThreadPoolException {
        if (this.execute) {
            throw new IllegalStateException("Threadpool not terminated before awaiting termination");
        }
        while (true) {
            boolean flag = true;
            for (Thread thread : threads) {
                if (thread.isAlive()) {
                    flag = false;
                    break;
                }
            }
            if (flag) {
                return;
            }
            try {
                Thread.sleep(1);
            } catch (InterruptedException e) {
                throw new ExecutionThreadPoolException(e);
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