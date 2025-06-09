package com.sandbox.synchronization;

/**
 * This class demonstrates two threads modifying a shared integer variable.
 * It shows how multiple threads can access and modify the same variable,
 * which can lead to race conditions without proper synchronization.
 */
public class SynchronizationFailureDemo {
    // Shared integer variable that will be modified by multiple threads
    private static int sharedCounter = 0;

    /**
     * Main method that creates two threads to modify the shared counter.
     * 
     * @param args command line arguments (not used)
     * @throws InterruptedException if any thread is interrupted
     */
    public static void main(String[] args) throws InterruptedException {
        System.out.println("Starting SynchronizationDemo with initial counter value: " + sharedCounter);

        // Create first thread that increments the counter
        Thread incrementThread = new Thread(() -> {
            for (int i = 0; i < 100000; i++) {
                sharedCounter++; // Increment the shared counter
                if (i % 1000 == 0) {
                    System.out.println("Increment thread: counter = " + sharedCounter);
                }
            }
            System.out.println("Increment thread completed. Final counter: " + sharedCounter);
        }, "IncrementThread");

        // Create second thread that decrements the counter
        Thread decrementThread = new Thread(() -> {
            for (int i = 0; i < 100000; i++) {
                sharedCounter--; // Decrement the shared counter
                if (i % 1000 == 0) {
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
        System.out.println("Note: The final value may not be 0 due to race conditions between the threads");
    }
}
