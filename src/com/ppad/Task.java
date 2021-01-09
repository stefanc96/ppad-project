package com.ppad;

public class Task implements Runnable {
    private final String name;

    public Task(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void run() {
        System.out.println("Executing : " + name);
    }
}