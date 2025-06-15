package com.sandbox.completablefuture;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static com.sandbox.completablefuture.CompletableFutureUtils.sleep;
import static com.sandbox.completablefuture.CompletableFutureUtils.shutdownAndAwaitTermination;

/**
 * This class demonstrates different ways to create CompletableFutures.
 * 
 * Example 1 from the CompletableFuture demos.
 */
public class CreatingCompletableFuturesDemo {

    /**
     * Main method that demonstrates different ways to create CompletableFutures.
     *
     * @param args command line arguments (not used)
     */
    public static void main(String[] args) {
        System.out.println("Starting Creating CompletableFutures demo");
        System.out.println("Main thread: " + Thread.currentThread().getName());

        // Create a custom executor for our async operations
        ExecutorService executor = Executors.newFixedThreadPool(4);

        try {
            System.out.println("\n1. Creating CompletableFutures");
            
            // 1.1 Creating a completed future
            CompletableFuture<String> completedFuture = CompletableFuture.completedFuture("Result is ready");
            System.out.println("Completed future result: " + completedFuture.get());
            
            // 1.2 Creating a future with supplyAsync
            CompletableFuture<String> supplyAsyncFuture = CompletableFuture.supplyAsync(() -> {
                System.out.println("Executing supplyAsync on thread: " + Thread.currentThread().getName());
                sleep(500);
                return "Result from supplyAsync";
            });
            
            // 1.3 Creating a future with runAsync (no result)
            CompletableFuture<Void> runAsyncFuture = CompletableFuture.runAsync(() -> {
                System.out.println("Executing runAsync on thread: " + Thread.currentThread().getName());
                sleep(500);
                System.out.println("runAsync task completed");
            });
            
            // 1.4 Creating a future with a custom executor
            CompletableFuture<String> customExecutorFuture = CompletableFuture.supplyAsync(() -> {
                System.out.println("Executing with custom executor on thread: " + Thread.currentThread().getName());
                sleep(500);
                return "Result from custom executor";
            }, executor);
            
            // Wait for the futures to complete
            System.out.println("supplyAsync result: " + supplyAsyncFuture.get());
            runAsyncFuture.get(); // No result to print
            System.out.println("customExecutor result: " + customExecutorFuture.get());
            
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