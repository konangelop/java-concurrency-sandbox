package com.sandbox.basicMultiithread;

/**
 * This class demonstrates the difference between daemon and user threads in Java.
 * 
 * Daemon threads are background threads that do not prevent the JVM from exiting
 * when the program finishes. They are typically used for background tasks like
 * garbage collection or service tasks that should run as long as the application
 * is running but don't need to complete before the application exits.
 * 
 * User threads (non-daemon threads) are the default thread type. The JVM will
 * wait for all user threads to complete before exiting, even if the main thread
 * has finished execution.
 */
public class DaemonUserThreadDemo {
    /**
     * Main method that creates and starts one daemon thread and one user thread.
     * The daemon thread will be terminated when the user thread completes and
     * the JVM exits, even though the daemon thread's work is not finished.
     * 
     * @param args command line arguments (not used)
     */
    public static void main(String[] args) {
        Thread daemonThread = new Thread(new DaemonHelper());
        Thread userThread = new Thread(new UserThread());

        // Set the thread as a daemon thread
        daemonThread.setDaemon(true);

        // Start both threads
        daemonThread.start();
        userThread.start();
    }
}

/**
 * A helper class that implements Runnable to create a daemon thread.
 * This thread attempts to run for a long time (500 iterations with delays),
 * but will be terminated when all user threads complete.
 */
class DaemonHelper implements Runnable {
    /**
     * The run method contains the code that will be executed in the daemon thread.
     * It counts up to 500 with a delay between each count, but will likely be
     * terminated before completion when the user thread finishes.
     */
    @Override
    public void run() {
        int count = 0;
        while (count < 500) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            count++;
            System.out.println("Daemon thread is running: " + count);
        }
    }
}

/**
 * A helper class that implements Runnable to create a user thread.
 * This thread sleeps for 5 seconds and then completes, which will
 * allow the JVM to exit and terminate any daemon threads.
 */
class UserThread implements Runnable {
    /**
     * The run method contains the code that will be executed in the user thread.
     * It simply sleeps for 5 seconds and then completes.
     */
    @Override
    public void run() {
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        System.out.println("User thread is finished");
    }
}
