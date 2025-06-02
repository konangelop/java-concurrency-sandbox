package com.sandbox.basicMultiithread;

/**
 * This class demonstrates creating and running multiple threads using the Runnable interface.
 * The Runnable interface is one of the two primary ways to create threads in Java
 * (the other being extending the Thread class).
 * <p>
 * Using Runnable is generally preferred over extending Thread because:
 * 1. It doesn't require subclassing, allowing the class to extend another class if needed
 * 2. It represents a task that can be executed by any thread, promoting better separation of concerns
 * 3. It can be used with thread pools and executor services
 */
public class RunnableThreadDemo {
    /**
     * Main method that demonstrates creating and starting three threads.
     * Two threads execute different Runnable implementations, while the third
     * uses a lambda expression as a Runnable implementation.
     * 
     * @param args command line arguments (not used)
     */
    public static void main(String[] args) {
        Thread firstThread = new Thread(new FirstThread());
        Thread secondThread = new Thread(new SecondThread());
        Thread thirdThread = new Thread(() -> {
            for (int i = 0; i < 15; i++) {
                System.out.println("Hello from third thread " + Thread.currentThread().getName() + "time: " + i);
            }
        });

        firstThread.start();
        secondThread.start();
        thirdThread.start();
    }
}

class FirstThread implements Runnable {

    @Override
    public void run() {
        for (int i = 0; i < 15; i++) {
            System.out.println("Hello from first thread " + Thread.currentThread().getName() + "time: " + i);
        }
    }
}

class SecondThread implements Runnable {
    @Override
    public void run() {
        for (int i = 0; i < 15; i++) {
            System.out.println("Hello from second thread " + Thread.currentThread().getName() + "time: " + i);
        }
    }
}
