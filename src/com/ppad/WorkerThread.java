package com.ppad;

public class WorkerThread extends Thread {
    private Boolean execute = true;
    private final BlockingQueue<Runnable> runnables;
    private final int keepAliveTime;
    private final ThreadType threadType;


    public WorkerThread(String name, BlockingQueue<Runnable> runnables, int keepAliveTime, ThreadType threadType) {
        super(name);
        this.runnables = runnables;
        this.keepAliveTime = keepAliveTime;
        this.threadType = threadType;
    }

    public void setExecute(boolean execute){
        this.execute = execute;
        System.out.println(execute);
    }

    @Override
    public void run() {
        long startExecution = 0;
        long endExecution;

        try {
            while (execute || runnables.isEmpty()) {
                Runnable runnable;
                if((runnable = runnables.poll()) != null) {
                    startExecution = System.currentTimeMillis();
                    runnable.run();
                }

                endExecution = System.currentTimeMillis();
                long totalTime = endExecution - startExecution;

                if(threadType == ThreadType.TEMPORARY && totalTime >= keepAliveTime){
                    System.out.println("Temporary thread stopped!");
                    return;
                }
            }
        } catch (RuntimeException | InterruptedException e) {
            throw new ExecutionThreadPoolException(e);
        }
    }
}
