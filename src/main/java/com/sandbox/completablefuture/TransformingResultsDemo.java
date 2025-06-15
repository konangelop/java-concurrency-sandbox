package com.sandbox.completablefuture;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static com.sandbox.completablefuture.CompletableFutureUtils.sleep;
import static com.sandbox.completablefuture.CompletableFutureUtils.shutdownAndAwaitTermination;

/**
 * This class demonstrates how to transform and process results from CompletableFutures.
 * 
 * Example 2 from the CompletableFuture demos.
 */
public class TransformingResultsDemo {

    /**
     * Main method that demonstrates transforming and processing results from CompletableFutures.
     *
     * @param args command line arguments (not used)
     */
    public static void main(String[] args) {
        System.out.println("Starting Transforming Results demo");
        System.out.println("Main thread: " + Thread.currentThread().getName());

        // Create a custom executor for our async operations
        ExecutorService executor = Executors.newFixedThreadPool(4);

        try {
            System.out.println("\n2. Transforming and processing results");
            
            // 2.1 thenApply - transform the result
            CompletableFuture<String> thenApplyFuture = CompletableFuture.supplyAsync(() -> {
                System.out.println("Executing initial task on thread: " + Thread.currentThread().getName());
                sleep(500);
                return 42;
            }).thenApply(result -> {
                System.out.println("Applying transformation on thread: " + Thread.currentThread().getName());
                return "The answer is: " + result;
            });
            
            // 2.2 thenAccept - consume the result without returning a value
            CompletableFuture<Void> thenAcceptFuture = CompletableFuture.supplyAsync(() -> {
                System.out.println("Executing task for thenAccept on thread: " + Thread.currentThread().getName());
                sleep(500);
                return "Hello, World!";
            }).thenAccept(result -> {
                System.out.println("Consuming result on thread: " + Thread.currentThread().getName());
                System.out.println("Received: " + result);
            });
            
            // 2.3 thenRun - run an action after completion, ignoring the result
            CompletableFuture<Void> thenRunFuture = CompletableFuture.supplyAsync(() -> {
                System.out.println("Executing task for thenRun on thread: " + Thread.currentThread().getName());
                sleep(500);
                return "This result will be ignored";
            }).thenRun(() -> {
                System.out.println("Running completion action on thread: " + Thread.currentThread().getName());
                System.out.println("Task completed, but result is ignored");
            });
            
            // 2.4 Async variants (thenApplyAsync, thenAcceptAsync, thenRunAsync)
            CompletableFuture<String> thenApplyAsyncFuture = CompletableFuture.supplyAsync(() -> {
                System.out.println("Executing initial task for thenApplyAsync on thread: " + Thread.currentThread().getName());
                sleep(500);
                return 100;
            }).thenApplyAsync(result -> {
                System.out.println("Applying async transformation on thread: " + Thread.currentThread().getName());
                sleep(500);
                return "Async result: " + result;
            }, executor);
            
            // Wait for the futures to complete
            System.out.println("thenApply result: " + thenApplyFuture.get());
            thenAcceptFuture.get(); // No result to print
            thenRunFuture.get(); // No result to print
            System.out.println("thenApplyAsync result: " + thenApplyAsyncFuture.get());
            
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