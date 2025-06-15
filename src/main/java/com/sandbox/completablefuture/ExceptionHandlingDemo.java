package com.sandbox.completablefuture;

import java.util.Random;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static com.sandbox.completablefuture.CompletableFutureUtils.sleep;
import static com.sandbox.completablefuture.CompletableFutureUtils.shutdownAndAwaitTermination;

/**
 * This class demonstrates exception handling in CompletableFutures.
 * 
 * Example 5 from the CompletableFuture demos.
 */
public class ExceptionHandlingDemo {

    /**
     * Main method that demonstrates exception handling in CompletableFutures.
     *
     * @param args command line arguments (not used)
     */
    public static void main(String[] args) {
        System.out.println("Starting Exception Handling demo");
        System.out.println("Main thread: " + Thread.currentThread().getName());

        // Create a custom executor for our async operations
        ExecutorService executor = Executors.newFixedThreadPool(4);

        try {
            System.out.println("\n5. Exception handling");
            
            // 5.1 exceptionally - recover from exceptions
            CompletableFuture<String> exceptionallyFuture = CompletableFuture.supplyAsync(() -> {
                System.out.println("Executing task that will fail on thread: " + Thread.currentThread().getName());
                sleep(500);
                if (true) { // Always throw exception for demo
                    throw new RuntimeException("Deliberate exception");
                }
                return "This will never be returned";
            }).exceptionally(ex -> {
                System.out.println("Handling exception on thread: " + Thread.currentThread().getName());
                System.out.println("Exception message: " + ex.getMessage());
                return "Recovered from: " + ex.getCause().getMessage();
            });
            
            // 5.2 handle - process both result and exception
            CompletableFuture<String> handleFuture = CompletableFuture.supplyAsync(() -> {
                System.out.println("Executing task for handle on thread: " + Thread.currentThread().getName());
                sleep(500);
                if (new Random().nextBoolean()) { // Randomly throw exception
                    throw new RuntimeException("Random failure");
                }
                return "Success result";
            }).handle((result, ex) -> {
                System.out.println("Handling result or exception on thread: " + Thread.currentThread().getName());
                if (ex != null) {
                    System.out.println("Exception occurred: " + ex.getMessage());
                    return "Handled exception: " + ex.getCause().getMessage();
                } else {
                    return "Handled success: " + result;
                }
            });
            
            // 5.3 whenComplete - process completion (with or without exception)
            CompletableFuture<String> whenCompleteFuture = CompletableFuture.supplyAsync(() -> {
                System.out.println("Executing task for whenComplete on thread: " + Thread.currentThread().getName());
                sleep(500);
                if (new Random().nextInt(10) > 7) { // 20% chance of failure
                    throw new RuntimeException("Occasional failure");
                }
                return "Task completed successfully";
            }).whenComplete((result, ex) -> {
                System.out.println("Completion handler on thread: " + Thread.currentThread().getName());
                if (ex != null) {
                    System.out.println("Task failed with exception: " + ex.getMessage());
                } else {
                    System.out.println("Task completed with result: " + result);
                }
            });
            
            // Wait for the futures to complete
            System.out.println("exceptionally result: " + exceptionallyFuture.get());
            System.out.println("handle result: " + handleFuture.get());
            try {
                System.out.println("whenComplete result: " + whenCompleteFuture.get());
            } catch (ExecutionException e) {
                System.out.println("whenComplete threw exception: " + e.getCause().getMessage());
            }
            
        } catch (InterruptedException e) {
            System.err.println("Main thread was interrupted: " + e.getMessage());
            Thread.currentThread().interrupt();
        } catch (ExecutionException e) {
            System.err.println("Execution exception: " + e.getMessage());
        } finally {
            // Proper shutdown of the executor service
            shutdownAndAwaitTermination(executor);
        }
        
        System.out.println("Demo completed");
    }
}