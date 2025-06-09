package com.sandbox.synchronization;

/**
 * This class demonstrates a solution to the race condition problem
 * by using a synchronized block to modify shared counters.
 * Each thread increments its own counter using synchronized blocks to ensure
 * that only one thread can modify a counter at a time.
 */
public class SynchronizationBlockDemo {
    // Two separate counters, each modified by a different thread
    private static int counter1 = 0;
    private static int counter2 = 0;

    // Object used for synchronization lock
    private static final Object LOCK_OBJECT = new Object();

    /**
     * Method to increment a specific counter using a synchronized block.
     * The synchronized block ensures that only one thread can execute
     * the critical section at a time, preventing race conditions.
     * 
     * @param counterNumber the counter to increment (1 or 2)
     */
    private static void incrementCounter(int counterNumber) {
        // Using a synchronized block on a dedicated lock object
        synchronized (LOCK_OBJECT) {
            if (counterNumber == 1) {
                counter1++; // Increment counter 1
            } else {
                counter2++; // Increment counter 2
            }
        }
    }

    /**
     * Main method that creates two threads to increment their respective counters.
     * 
     * @param args command line arguments (not used)
     * @throws InterruptedException if any thread is interrupted
     */
    public static void main(String[] args) throws InterruptedException {
        System.out.println("Starting SynchronizationBlockDemo with initial counter values: counter1 = " + counter1 + ", counter2 = " + counter2);

        // Create first thread that increments counter1
        Thread thread1 = new Thread(() -> {
            for (int i = 0; i < 100000; i++) {
                incrementCounter(1); // Call method with synchronized block to increment counter1
                if (i % 10000 == 0) {
                    System.out.println("Thread 1: counter1 = " + counter1);
                }
            }
            System.out.println("Thread 1 completed. Final counter1: " + counter1);
        }, "Thread1");

        // Create second thread that increments counter2
        Thread thread2 = new Thread(() -> {
            for (int i = 0; i < 100000; i++) {
                incrementCounter(2); // Call method with synchronized block to increment counter2
                if (i % 10000 == 0) {
                    System.out.println("Thread 2: counter2 = " + counter2);
                }
            }
            System.out.println("Thread 2 completed. Final counter2: " + counter2);
        }, "Thread2");

        // Start both threads
        thread1.start();
        thread2.start();

        // Wait for both threads to complete
        thread1.join();
        thread2.join();

        // Display the final values of the counters
        System.out.println("Both threads have completed execution");
        System.out.println("Final counter values: counter1 = " + counter1 + ", counter2 = " + counter2);
        System.out.println("Note: Both counters should be 100000 because we used proper synchronization with a synchronized block on a dedicated lock object");
    }
}
