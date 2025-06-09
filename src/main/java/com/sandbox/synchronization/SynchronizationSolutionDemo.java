package com.sandbox.synchronization;

/**
 * This class demonstrates a solution to the race condition problem
 * by using a synchronized method to modify a shared counter.
 * Both threads call the same method to change the counter value,
 * ensuring that only one thread can modify the counter at a time.
 */
public class SynchronizationSolutionDemo {
    // Shared integer variable that will be modified by multiple threads
    private static int sharedCounter = 0;

    /**
     * Synchronized method to modify the counter value.
     * The synchronized keyword ensures that only one thread can execute
     * this method at a time, preventing race conditions.
     * 
     * @param increment true to increment the counter, false to decrement
     */
    private static synchronized void modifyCounter(boolean increment) {
        if (increment) {
            sharedCounter++; // Increment the shared counter
        } else {
            sharedCounter--; // Decrement the shared counter
        }
    }

    /**
     * Main method that creates two threads to modify the shared counter.
     * 
     * @param args command line arguments (not used)
     * @throws InterruptedException if any thread is interrupted
     */
    public static void main(String[] args) throws InterruptedException {
        System.out.println("Starting SynchronizationSolutionDemo with initial counter value: " + sharedCounter);

        // Create first thread that increments the counter
        Thread incrementThread = new Thread(() -> {
            for (int i = 0; i < 100000; i++) {
                modifyCounter(true); // Call synchronized method to increment
                if (i % 10000 == 0) {
                    System.out.println("Increment thread: counter = " + sharedCounter);
                }
            }
            System.out.println("Increment thread completed. Final counter: " + sharedCounter);
        }, "IncrementThread");

        // Create second thread that decrements the counter
        Thread decrementThread = new Thread(() -> {
            for (int i = 0; i < 100000; i++) {
                modifyCounter(false); // Call synchronized method to decrement
                if (i % 10000 == 0) {
                    System.out.println("Decrement thread: counter = " + sharedCounter);
                }
            }
            System.out.println("Decrement thread completed. Final counter: " + sharedCounter);
        }, "DecrementThread");

        // Start both threads
        incrementThread.start();
        decrementThread.start();

        // Wait for both threads to complete
        incrementThread.join();
        decrementThread.join();

        // Display the final value of the counter
        System.out.println("Both threads have completed execution");
        System.out.println("Final counter value: " + sharedCounter);
        System.out.println("Note: The final value should be 0 because we used proper synchronization");
    }
}