package com.sandbox.basicMultiithread;

/**
 * This class demonstrates the impact of setting different thread priorities.
 * In Java, thread priorities range from Thread.MIN_PRIORITY (1) to Thread.MAX_PRIORITY (10),
 * with Thread.NORM_PRIORITY (5) being the default.
 * 
 * Note: The actual impact of thread priorities depends on the operating system and JVM implementation.
 * Some operating systems might ignore thread priorities entirely.
 */
public class ThreadPriorityDemo {

    /**
     * Main method that creates threads with different priorities and demonstrates their impact.
     * 
     * @param args command line arguments (not used)
     */
    public static void main(String[] args) {
        // Create worker threads with different priorities
        PriorityWorker lowPriorityWorker = new PriorityWorker("Low-Priority-Thread");
        PriorityWorker normalPriorityWorker = new PriorityWorker("Normal-Priority-Thread");
        PriorityWorker highPriorityWorker = new PriorityWorker("High-Priority-Thread");

        // Set different priorities
        lowPriorityWorker.setPriority(Thread.MIN_PRIORITY); // 1
        // normalPriorityWorker uses default priority (5)
        highPriorityWorker.setPriority(Thread.MAX_PRIORITY); // 10

        System.out.println("Starting threads with different priorities...");
        System.out.println("Low Priority Thread: " + lowPriorityWorker.getPriority());
        System.out.println("Normal Priority Thread: " + normalPriorityWorker.getPriority());
        System.out.println("High Priority Thread: " + highPriorityWorker.getPriority());

        // Start the threads
        lowPriorityWorker.start();
        normalPriorityWorker.start();
        highPriorityWorker.start();

        try {
            // Wait for all threads to complete
            lowPriorityWorker.join();
            normalPriorityWorker.join();
            highPriorityWorker.join();
        } catch (InterruptedException e) {
            System.out.println("Main thread interrupted: " + e.getMessage());
        }

        // Display the results
        System.out.println("\nExecution completed. Results:");
        System.out.println("Low Priority Thread count: " + lowPriorityWorker.getCounter());
        System.out.println("Normal Priority Thread count: " + normalPriorityWorker.getCounter());
        System.out.println("High Priority Thread count: " + highPriorityWorker.getCounter());
    }

    /**
     * A thread class that counts how many iterations it can perform in a fixed time period.
     * This helps demonstrate the impact of thread priorities on CPU time allocation.
     */
    static class PriorityWorker extends Thread {
        private long counter = 0;

        public PriorityWorker(String name) {
            super(name);
        }

        @Override
        public void run() {
            System.out.println(getName() + " started with priority " + getPriority());

            // Run for a fixed amount of time (3 seconds)
            long endTime = System.currentTimeMillis() + 3000;

            boolean running = true;
            while (System.currentTimeMillis() < endTime) {
                counter++;

                // Print progress every 10 million iterations
                if (counter % 10_000_000 == 0) {
                    System.out.println(getName() + " reached " + counter + " iterations");
                }
            }

            System.out.println(getName() + " finished with " + counter + " iterations");
        }

        public long getCounter() {
            return counter;
        }
    }
}
