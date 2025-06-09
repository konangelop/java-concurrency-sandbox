package com.sandbox.executors;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * This class demonstrates how to run tasks sequentially using a single thread executor.
 * 
 * A single thread executor creates a single worker thread to process tasks from a queue.
 * Tasks are guaranteed to execute sequentially in the order they are submitted.
 * This is useful when you need to ensure that tasks don't execute concurrently
 * but still want to leverage the executor framework for task management.
 */
public class SingleThreadExecutorDemo {

    /**
     * Main method that demonstrates sequential execution of tasks using a single thread executor.
     * 
     * @param args command line arguments (not used)
     */
    public static void main(String[] args) {
        System.out.println("Starting single thread executor demo");
        
        // Create a single thread executor
        ExecutorService executor = Executors.newSingleThreadExecutor();
        
        try {
            System.out.println("Main thread: " + Thread.currentThread().getName());
            
            // Submit multiple tasks to the executor
            // These tasks will be executed sequentially by the single worker thread
            for (int i = 1; i <= 5; i++) {
                final int taskId = i;
                executor.submit(() -> performTask(taskId));
                System.out.println("Task " + taskId + " submitted");
            }
            
            System.out.println("All tasks have been submitted");
            System.out.println("Main thread continues execution without waiting for tasks to complete");
            
            // Demonstrate that we can submit more tasks later and they'll still be executed in order
            Thread.sleep(3000);
            System.out.println("\nSubmitting additional tasks after delay");
            
            for (int i = 6; i <= 8; i++) {
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
     * This method prints the thread name to demonstrate that all tasks
     * are executed by the same worker thread.
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