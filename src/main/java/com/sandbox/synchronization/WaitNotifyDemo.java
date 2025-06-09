package com.sandbox.synchronization;

/**
 * This class demonstrates the wait/notify mechanism for thread synchronization.
 * It creates two threads that are synchronized over a shared lock object.
 * One thread calls wait() on the lock, causing it to pause execution until
 * the other thread calls notify() on the same lock.
 */
public class WaitNotifyDemo {
    // Object used as a lock for synchronization
    private static final Object LOCK = new Object();

    // Message to be passed between threads
    private static String message = null;

    /**
     * Main method that creates and starts the waiting and notifying threads.
     * 
     * @param args command line arguments (not used)
     * @throws InterruptedException if any thread is interrupted
     */
    public static void main(String[] args) throws InterruptedException {
        System.out.println("Starting WaitNotifyDemo");

        // Create the waiting thread
        Thread waitingThread = new Thread(() -> {
            synchronized (LOCK) {
                System.out.println("Waiting Thread: Acquired the lock");

                try {
                    System.out.println("Waiting Thread: Going to wait...");
                    LOCK.wait(); // Release the lock and wait for notification
                    System.out.println("Waiting Thread: Woke up from wait state");
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    System.err.println("Waiting Thread: Interrupted while waiting: " + e.getMessage());
                }

                // Process the message after being notified
                System.out.println("Waiting Thread: Received message: " + message);
            }
            System.out.println("Waiting Thread: Finished execution");
        }, "WaitingThread");

        // Create the notifying thread
        Thread notifyingThread = new Thread(() -> {
            synchronized (LOCK) {
                System.out.println("Notifying Thread: Acquired the lock");

                // Set the message
                message = "Hello from the notifying thread!";

                // Notify the waiting thread
                System.out.println("Notifying Thread: Sending notification with message: " + message);
                LOCK.notify();

                // Note that the waiting thread won't wake up until this thread releases the lock
                System.out.println("Notifying Thread: Notification sent, about to release the lock");
            }

            System.out.println("Notifying Thread: Released the lock, waiting thread can now proceed");
        }, "NotifyingThread");

        // Start both threads
        waitingThread.start();
        notifyingThread.start();

        // Wait for both threads to complete
        waitingThread.join();
        notifyingThread.join();

        System.out.println("WaitNotifyDemo completed");
    }
}
