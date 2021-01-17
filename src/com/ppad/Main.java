package com.ppad;

public class Main {
    public static void main(String[] args) {
        ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(2, 15, 5, 10);

        threadPoolExecutor.addWorker();
        threadPoolExecutor.addWorker();
        threadPoolExecutor.addWorker();
        threadPoolExecutor.addWorker();
        threadPoolExecutor.addWorker();

        for (int i = 1; i <= 20; i++) {
            Task task = new Task("Task " + i);
            System.out.println("Created : " + task.getName());

            threadPoolExecutor.execute(task);
        }
        try {
            threadPoolExecutor.awaitTermination();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}