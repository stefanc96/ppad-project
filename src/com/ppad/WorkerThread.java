package com.ppad;

public class WorkerThread extends Thread {
    private Boolean execute;
    private final BlockingQueue<Runnable> runnables;
    private final int keepAliveTime;


    public WorkerThread(String name, Boolean execute, BlockingQueue<Runnable> runnables, int keepAliveTime) {
        super(name);
        this.execute = execute;
        this.runnables = runnables;
        this.keepAliveTime = keepAliveTime;
    }

    public void setExecute(boolean execute){
        this.execute = execute;
        System.out.println(execute);
    }

    @Override
    public void run() {
        try {
            while (execute || !runnables.isEmpty()) {
                Runnable runnable;

                while ((runnable = runnables.poll()) != null) {
                    System.out.println(runnables.toString());
                    runnable.run();
                }

                Thread.sleep(keepAliveTime);
            }
        } catch (RuntimeException | InterruptedException e) {
            throw new ExecutionThreadPoolException(e);
        }
    }
}
