package com.ppad;

import java.util.Queue;

public class ExecutionThread extends Thread {
    private final Boolean execute;
    private final Queue<Runnable> runnables;

    public ExecutionThread(String name, Boolean execute, Queue<Runnable> runnables) {
        super(name);
        this.execute = execute;
        this.runnables = runnables;
    }

    @Override
    public void run() {
        try {
            // Continue to execute when the execute flag is true, or when there are runnables in the queue
            while (execute || !runnables.isEmpty()) {
                Runnable runnable;
                // Poll a runnable from the queue and execute it
                while ((runnable = runnables.poll()) != null) {
                    runnable.run();
                }
                // Sleep in case there wasn't any runnable in the queue. This helps to avoid hogging the CPU.
                Thread.sleep(1);
            }
        } catch (RuntimeException | InterruptedException e) {
            throw new ExecutionThreadPoolException(e);
        }
    }
}
