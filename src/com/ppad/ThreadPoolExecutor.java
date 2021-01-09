package com.ppad;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class ThreadPoolExecutor {
    // Count of threadpools created
    private static volatile Integer poolCount = 0;
    // Queue of runnables
    private final Queue<Runnable> runnables;
    private int corePoolSize;
    private int maximumPoolSize;
    private int keepAliveTime;
    private int queueSize;
    // Flag to control the ExecutionThreadPool objects
    private volatile Boolean execute;
    // Holds the "pool" of threads
    private final List<ExecutionThread> threads;

    /**
     * Private constructor to control the creation of threadpools. Increments the poolcount whenever a new pool is created.
     *
     * @param threadCount Number of ExecutionThreadPools to add to the pool
     */
    private ThreadPoolExecutor(int threadCount) {
        // Increment pool count
        poolCount++;
        this.runnables = new LinkedList<>();
        this.execute = true;
        this.threads = new ArrayList<>();
        for (int threadIndex = 0; threadIndex < threadCount; threadIndex++) {
            ExecutionThread thread = new ExecutionThread("SimpleThreadpool" + poolCount + "Thread" + threadIndex, this.execute, this.runnables);
            thread.start();
            this.threads.add(thread);
        }
    }

    /**
     * Gets a new threadpool instance with the number of threads specified
     *
     * @param threadCount Threads to add to the pool
     * @return new SimpleThreadpool
     */
    public static ThreadPoolExecutor newFixedThreadPool(int threadCount) {
        return new ThreadPoolExecutor(threadCount);
    }

    /**
     * Adds a runnable to the queue for processing
     *
     * @param runnable Runnable to be added to the pool
     */
    public void execute(Runnable runnable) {
        if (this.execute) {
            runnables.add(runnable);
        } else {
            throw new IllegalStateException("Threadpool terminating, unable to execute runnable");
        }
    }

    /**
     * Awaits up to <b>timeout</b> ms the termination of the threads in the threadpool
     *
     * @param timeout Timeout in milliseconds
     * @throws Exception             Thrown if the termination takes longer than the timeout
     * @throws IllegalStateException Thrown if the stop() or terminate() methods haven't been called before awaiting
     */
    public void awaitTermination(long timeout) throws Exception {
        if (this.execute) {
            throw new IllegalStateException("Threadpool not terminated before awaiting termination");
        }
        long startTime = System.currentTimeMillis();
        while (System.currentTimeMillis() - startTime <= timeout) {
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
        throw new Exception("Unable to terminate threadpool within the specified timeout (" + timeout + "ms)");
    }

    /**
     * Awaits the termination of the threads in the threadpool indefinitely
     *
     * @throws IllegalStateException Thrown if the stop() or terminate() methods haven't been called before awaiting
     */
    public void awaitTermination() throws Exception {
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

    /**
     * Clears the queue of runnables and stops the threadpool. Any runnables currently executing will continue to execute.
     */
    public void terminate() {
        runnables.clear();
        stop();
    }

    /**
     * Stops addition of new runnables to the threadpool and terminates the pool once all runnables in the queue are executed.
     */
    public void stop() {
        execute = false;
    }
}