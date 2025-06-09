package com.sandbox.executors;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

/**
 * This class demonstrates how to run tasks using a scheduled thread pool executor.
 * 
 * A scheduled thread pool executor creates a fixed-size thread pool that can schedule tasks
 * to run after a given delay or to execute periodically. This is useful for tasks that need
 * to run at specific times or with specific intervals, such as:
 * - Running maintenance tasks at fixed intervals
 * - Executing delayed tasks
 * - Implementing timeouts
 * - Scheduling recurring jobs
 */
public class ScheduledThreadPoolDemo {

    /**
     * Main method that demonstrates execution of tasks using a scheduled thread pool executor.
     * 
     * @param args command line arguments (not used)
     */
    public static void main(String[] args) {
        System.out.println("Starting scheduled thread pool executor demo");
        
        // Create a scheduled thread pool with 3 threads
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(3);
        
        try {
            System.out.println("Main thread: " + Thread.currentThread().getName());
            System.out.println("Current time: " + System.currentTimeMillis() + "ms");
            
            // Schedule a one-time task to run after a delay
            System.out.println("\nScheduling a one-time delayed task");
            final int delayedTaskId = 1;
            scheduler.schedule(
                () -> performTask(delayedTaskId), 
                2, 
                TimeUnit.SECONDS
            );
            System.out.println("Task " + delayedTaskId + " scheduled to run after 2 seconds");
            
            // Schedule a task to run repeatedly with a fixed delay between the end of the previous execution and the start of the next
            System.out.println("\nScheduling a fixed-delay repeated task");
            final int fixedDelayTaskId = 2;
            scheduler.scheduleWithFixedDelay(
                () -> performTask(fixedDelayTaskId),
                1,  // initial delay
                3,  // delay between tasks
                TimeUnit.SECONDS
            );
            System.out.println("Task " + fixedDelayTaskId + " scheduled to run repeatedly with 3 seconds delay between executions");
            
            // Schedule a task to run repeatedly at a fixed rate
            System.out.println("\nScheduling a fixed-rate repeated task");
            final int fixedRateTaskId = 3;
            ScheduledFuture<?> fixedRateTask = scheduler.scheduleAtFixedRate(
                () -> performTask(fixedRateTaskId),
                1,  // initial delay
                2,  // period between tasks
                TimeUnit.SECONDS
            );
            System.out.println("Task " + fixedRateTaskId + " scheduled to run repeatedly every 2 seconds");
            
            // Schedule a one-time task that will cancel the fixed-rate task after 10 seconds
            scheduler.schedule(() -> {
                System.out.println("\nCancelling the fixed-rate task");
                fixedRateTask.cancel(false);
                System.out.println("Fixed-rate task cancelled");
            }, 10, TimeUnit.SECONDS);
            
            // Let the demo run for a while to observe the scheduled tasks
            Thread.sleep(20000);
            
        } catch (InterruptedException e) {
            System.err.println("Main thread was interrupted: " + e.getMessage());
            Thread.currentThread().interrupt();
        } finally {
            // Proper shutdown of the scheduler service
            shutdownAndAwaitTermination(scheduler);
        }
        
        System.out.println("Demo completed");
    }
    
    /**
     * Simulates performing a task by sleeping for a short time.
     * This method prints the thread name and current time to demonstrate
     * when tasks are executed by the scheduler.
     * 
     * @param taskId the ID of the task being performed
     */
    private static void performTask(int taskId) {
        String threadName = Thread.currentThread().getName();
        long currentTime = System.currentTimeMillis();
        
        System.out.println("Task " + taskId + " started on thread: " + threadName + " at time: " + currentTime + "ms");
        
        try {
            // Simulate task execution time
            Thread.sleep(1000);
            System.out.println("Task " + taskId + " completed on thread: " + threadName + " at time: " + System.currentTimeMillis() + "ms");
        } catch (InterruptedException e) {
            System.err.println("Task " + taskId + " was interrupted: " + e.getMessage());
            Thread.currentThread().interrupt();
        }
    }
    
    /**
     * Properly shuts down a ScheduledExecutorService in two phases:
     * 1. Disable new tasks from being submitted
     * 2. Cancel currently executing tasks
     * 
     * @param scheduler the scheduler service to shut down
     */
    private static void shutdownAndAwaitTermination(ScheduledExecutorService scheduler) {
        // Disable new tasks from being submitted
        scheduler.shutdown();
        try {
            // Wait for existing tasks to terminate
            if (!scheduler.awaitTermination(10, TimeUnit.SECONDS)) {
                // Cancel currently executing tasks forcefully
                scheduler.shutdownNow();
                // Wait a while for tasks to respond to being cancelled
                if (!scheduler.awaitTermination(10, TimeUnit.SECONDS)) {
                    System.err.println("Scheduler did not terminate");
                }
            }
        } catch (InterruptedException e) {
            // (Re-)Cancel if current thread also interrupted
            scheduler.shutdownNow();
            // Preserve interrupt status
            Thread.currentThread().interrupt();
        }
    }
}