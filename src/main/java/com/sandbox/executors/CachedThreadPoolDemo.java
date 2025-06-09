package com.sandbox.executors;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * This class demonstrates how to run tasks using a cached thread pool executor.
 * 
 * A cached thread pool executor creates new threads as needed, but will reuse previously
 * constructed threads when they are available. These pools will typically improve the
 * performance of programs that execute many short-lived asynchronous tasks.
 * 
 * Threads that have not been used for 60 seconds are terminated and removed from the cache.
 * Unlike fixed thread pools, cached thread pools can grow unbounded if all threads are busy
 * and new tasks are submitted.
 */
public class CachedThreadPoolDemo {

    /**
     * Main method that demonstrates execution of tasks using a cached thread pool executor.
     * 
     * @param args command line arguments (not used)
     */
    public static void main(String[] args) {
        System.out.println("Starting cached thread pool executor demo");
        
        // Create a cached thread pool
        ExecutorService executor = Executors.newCachedThreadPool();
        
        try {
            System.out.println("Main thread: " + Thread.currentThread().getName());
            
            // Submit first batch of tasks to the executor
            // These tasks will cause the pool to create new threads as needed
            System.out.println("\nSubmitting first batch of tasks");
            for (int i = 1; i <= 1000; i++) {
                final int taskId = i;
                executor.submit(() -> performTask(taskId, 1000)); // 1 second tasks
                System.out.println("Task " + taskId + " submitted");
            }
            
            System.out.println("First batch of tasks have been submitted");
            
            // Wait a bit to allow some tasks to complete
            Thread.sleep(3000);
            
            // Submit second batch of tasks
            // Some of these tasks should reuse threads from the first batch
            System.out.println("\nSubmitting second batch of tasks");
            for (int i = 1001; i <= 1500; i++) {
                final int taskId = i;
                executor.submit(() -> performTask(taskId, 1000)); // 1 second tasks
                System.out.println("Task " + taskId + " submitted");
            }
            
            // Wait longer than the keep-alive time (60 seconds) to demonstrate thread termination
            // For demo purposes, we'll wait just a short time
            Thread.sleep(3000);
            
            // Submit third batch with longer execution time
            // This demonstrates that the pool can handle varying task durations
            System.out.println("\nSubmitting third batch of tasks with longer duration");
            for (int i = 16; i <= 18; i++) {
                final int taskId = i;
                executor.submit(() -> performTask(taskId, 2000)); // 2 second tasks
                System.out.println("Task " + taskId + " submitted");
            }
            
        } catch (InterruptedException e) {
            System.err.println("Main thread was interrupted: " + e.getMessage());
            Thread.currentThread().interrupt();
        } finally {
            // Proper shutdown of the executor service
            shutdownAndAwaitTermination(executor);
        }
        
        System.out.println("Demo completed");
    }
    
    /**
     * Simulates performing a task by sleeping for the specified time.
     * This method prints the thread name to demonstrate that tasks
     * may be executed by different threads in the pool.
     * 
     * @param taskId the ID of the task being performed
     * @param duration the duration in milliseconds that the task should take
     */
    private static void performTask(int taskId, long duration) {
        String threadName = Thread.currentThread().getName();
        System.out.println("Task " + taskId + " started on thread: " + threadName);
        
        try {
            // Simulate task execution time
            Thread.sleep(duration);
            System.out.println("Task " + taskId + " completed on thread: " + threadName);
        } catch (InterruptedException e) {
            System.err.println("Task " + taskId + " was interrupted: " + e.getMessage());
            Thread.currentThread().interrupt();
        }
    }
    
    /**
     * Properly shuts down an ExecutorService in two phases:
     * 1. Disable new tasks from being submitted
     * 2. Cancel currently executing tasks
     * 
     * @param executor the executor service to shut down
     */
    private static void shutdownAndAwaitTermination(ExecutorService executor) {
        // Disable new tasks from being submitted
        executor.shutdown();
        try {
            // Wait for existing tasks to terminate
            if (!executor.awaitTermination(10, TimeUnit.SECONDS)) {
                // Cancel currently executing tasks forcefully
                executor.shutdownNow();
                // Wait a while for tasks to respond to being cancelled
                if (!executor.awaitTermination(10, TimeUnit.SECONDS)) {
                    System.err.println("Executor did not terminate");
                }
            }
        } catch (InterruptedException e) {
            // (Re-)Cancel if current thread also interrupted
            executor.shutdownNow();
            // Preserve interrupt status
            Thread.currentThread().interrupt();
        }
    }
}