package com.sandbox.executors;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * This class demonstrates how to run tasks in parallel using a fixed thread pool executor.
 * 
 * A fixed thread pool executor creates a specified number of worker threads to process tasks from a queue.
 * Tasks are executed concurrently up to the thread pool size, with additional tasks queued until a thread becomes available.
 * This is useful when you want to limit the number of concurrent threads to avoid overwhelming system resources
 * while still processing multiple tasks in parallel.
 */
public class FixedThreadPoolDemo {

    /**
     * Main method that demonstrates parallel execution of tasks using a fixed thread pool executor.
     * 
     * @param args command line arguments (not used)
     */
    public static void main(String[] args) {
        System.out.println("Starting fixed thread pool executor demo");
        
        // Create a fixed thread pool with 3 threads
        ExecutorService executor = Executors.newFixedThreadPool(3);
        
        try {
            System.out.println("Main thread: " + Thread.currentThread().getName());
            
            // Submit multiple tasks to the executor
            // These tasks will be executed in parallel by the worker threads in the pool
            for (int i = 1; i <= 15; i++) {
                final int taskId = i;
                executor.submit(() -> performTask(taskId));
                System.out.println("Task " + taskId + " submitted");
            }
            
            System.out.println("All tasks have been submitted");
            System.out.println("Main thread continues execution without waiting for tasks to complete");
            
            // Demonstrate that we can submit more tasks later
            Thread.sleep(3000);
            System.out.println("\nSubmitting additional tasks after delay");
            
            for (int i = 16; i <= 18; i++) {
                final int taskId = i;
                executor.submit(() -> performTask(taskId));
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
     * Simulates performing a task by sleeping for a short time.
     * This method prints the thread name to demonstrate that tasks
     * are executed by different worker threads in the pool.
     * 
     * @param taskId the ID of the task being performed
     */
    private static void performTask(int taskId) {
        String threadName = Thread.currentThread().getName();
        System.out.println("Task " + taskId + " started on thread: " + threadName);
        
        try {
            // Simulate task execution time
            Thread.sleep(1000);
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