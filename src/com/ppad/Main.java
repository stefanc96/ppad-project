package com.ppad;

import java.util.concurrent.Executors;

public class Main {
    public static void main(String[] args) {
        java.util.concurrent.ThreadPoolExecutor executor = (java.util.concurrent.ThreadPoolExecutor) Executors.newFixedThreadPool(2);
        ThreadPoolExecutor threadPoolExecutor = ThreadPoolExecutor.newFixedThreadPool(2);

        for (int i = 1; i <= 20; i++) {
            Task task = new Task("Task " + i);
            System.out.println("Created : " + task.getName());

            threadPoolExecutor.execute(task);
        }
    }
}