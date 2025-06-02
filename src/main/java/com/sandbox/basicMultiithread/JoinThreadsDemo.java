package com.sandbox.basicMultiithread;

/**
 * This class demonstrates creating threads using lambda expressions and
 * joining them to wait for their completion.
 * 
 * The join() method causes the current thread to pause execution until
 * the thread it's called on completes its execution.
 */
public class JoinThreadsDemo {

    /**
     * Main method that creates two threads using lambda expressions,
     * starts them, and then joins them to wait for their completion.
     * 
     * @param args command line arguments (not used)
     * @throws InterruptedException if any thread is interrupted
     */
    public static void main(String[] args) throws InterruptedException {
        // Create first thread using lambda expression
        Thread firstThread = new Thread(() -> {
            for (int i = 0; i < 5; i++) {
                System.out.println("First thread executing: " + i);
                try {
                    // Sleep to simulate some work
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    System.out.println("First thread interrupted");
                    Thread.currentThread().interrupt();
                }
            }
            System.out.println("First thread completed");
        });

        // Create second thread using lambda expression
        Thread secondThread = new Thread(() -> {
            for (int i = 0; i < 5; i++) {
                System.out.println("Second thread executing: " + i);
                try {
                    // Sleep to simulate some work
                    Thread.sleep(150);
                } catch (InterruptedException e) {
                    System.out.println("Second thread interrupted");
                    Thread.currentThread().interrupt();
                }
            }
            System.out.println("Second thread completed");
        });

        System.out.println("Starting threads");

        // Start both threads
        firstThread.start();
        secondThread.start();

        System.out.println("Threads started, now waiting for them to complete");

        // Join both threads - main thread will wait until both threads complete
        firstThread.join();
        System.out.println("First thread joined");

        secondThread.join();
        System.out.println("Second thread joined");

        System.out.println("All threads have completed execution");
    }
}
