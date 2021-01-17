package com.ppad;

public class WorkerThread extends Thread {
    private Boolean isActive = true;
    private final BlockingQueue<Runnable> runnables;
    private final int keepAliveTime;
    private final ThreadType threadType;


    public WorkerThread(String name, BlockingQueue<Runnable> runnables, int keepAliveTime, ThreadType threadType) {
        super(name);
        this.runnables = runnables;
        this.keepAliveTime = keepAliveTime;
        this.threadType = threadType;
    }

    public void setIsActive(boolean isActive) {
        this.isActive = isActive;
    }

    @Override
    public void run() {
        long startExecution = 0;
        long endExecution;

        try {
            while (isActive || !runnables.isEmpty()) {
                Runnable runnable;
                if((runnable = runnables.poll()) != null) {
                    startExecution = System.currentTimeMillis();
                    runnable.run();
                }

                endExecution = System.currentTimeMillis();
                long totalTime = endExecution - startExecution;

                System.out.println(totalTime);
                if (threadType == ThreadType.TEMPORARY && totalTime >= keepAliveTime) {
                    System.out.println("Temporary thread stopped: " + getName());
                    return;
                }
            }
            System.out.println("Finished core thread: " + getName());
        } catch (RuntimeException | InterruptedException e) {
            throw new ExecutionThreadPoolException(e);
        }
    }
}
