package com.sandbox.completablefuture;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static com.sandbox.completablefuture.CompletableFutureUtils.sleep;
import static com.sandbox.completablefuture.CompletableFutureUtils.shutdownAndAwaitTermination;

/**
 * This class demonstrates manual completion of CompletableFutures.
 * 
 * Example 7 from the CompletableFuture demos.
 */
public class ManualCompletionDemo {

    /**
     * Main method that demonstrates manual completion of CompletableFutures.
     *
     * @param args command line arguments (not used)
     */
    public static void main(String[] args) {
        System.out.println("Starting Manual Completion demo");
        System.out.println("Main thread: " + Thread.currentThread().getName());

        // Create a custom executor for our async operations
        ExecutorService executor = Executors.newFixedThreadPool(4);

        try {
            System.out.println("\n7. Manual completion");
            
            // 7.1 complete - manually complete a future
            CompletableFuture<String> manualFuture = new CompletableFuture<>();
            
            // Start a task that will manually complete the future
            CompletableFuture.runAsync(() -> {
                System.out.println("Task that will manually complete the future on thread: " + Thread.currentThread().getName());
                sleep(1000);
                System.out.println("Manually completing the future");
                manualFuture.complete("Manually completed result");
            });
            
            // 7.2 completeExceptionally - manually complete a future with an exception
            CompletableFuture<String> manualExceptionFuture = new CompletableFuture<>();
            
            // Start a task that will manually complete the future with an exception
            CompletableFuture.runAsync(() -> {
                System.out.println("Task that will manually fail the future on thread: " + Thread.currentThread().getName());
                sleep(1000);
                System.out.println("Manually failing the future");
                manualExceptionFuture.completeExceptionally(new RuntimeException("Manually triggered exception"));
            });
            
            // Wait for the futures to complete
            System.out.println("manualFuture result: " + manualFuture.get());
            
            try {
                System.out.println("manualExceptionFuture result: " + manualExceptionFuture.get());
            } catch (ExecutionException e) {
                System.out.println("manualExceptionFuture threw exception: " + e.getCause().getMessage());
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