package com.ppad;

import java.util.Queue;

public class ExecutionThread extends Thread {
    private final Boolean execute;
    private final Queue<Runnable> runnables;
    private final int keepAliveTime;


    public ExecutionThread(String name, Boolean execute, Queue<Runnable> runnables, int keepAliveTime) {
        super(name);
        this.execute = execute;
        this.runnables = runnables;
        this.keepAliveTime = keepAliveTime;
    }

    @Override
    public void run() {
        try {
            while (execute || !runnables.isEmpty()) {
                Runnable runnable;

                while ((runnable = runnables.poll()) != null) {
                    runnable.run();
                }

                Thread.sleep(keepAliveTime);
            }
        } catch (RuntimeException | InterruptedException e) {
            throw new ExecutionThreadPoolException(e);
        }
    }
}
