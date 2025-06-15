package com.sandbox.completablefuture;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static com.sandbox.completablefuture.CompletableFutureUtils.sleep;
import static com.sandbox.completablefuture.CompletableFutureUtils.shutdownAndAwaitTermination;

/**
 * This class demonstrates how to chain and compose CompletableFutures.
 * 
 * Example 3 from the CompletableFuture demos.
 */
public class ChainingFuturesDemo {

    /**
     * Main method that demonstrates chaining and composing CompletableFutures.
     *
     * @param args command line arguments (not used)
     */
    public static void main(String[] args) {
        System.out.println("Starting Chaining Futures demo");
        System.out.println("Main thread: " + Thread.currentThread().getName());

        // Create a custom executor for our async operations
        ExecutorService executor = Executors.newFixedThreadPool(4);

        try {
            System.out.println("\n3. Chaining and composing futures");
            
            // 3.1 Chaining multiple transformations
            CompletableFuture<String> chainedFuture = CompletableFuture.supplyAsync(() -> {
                System.out.println("Executing first task in chain on thread: " + Thread.currentThread().getName());
                sleep(500);
                return 10;
            }).thenApply(result -> {
                System.out.println("Executing second task in chain on thread: " + Thread.currentThread().getName());
                return result * 2;
            }).thenApply(result -> {
                System.out.println("Executing third task in chain on thread: " + Thread.currentThread().getName());
                return "Final result: " + result;
            });
            
            // 3.2 thenCompose - flat mapping (similar to flatMap in streams)
            CompletableFuture<String> thenComposeFuture = CompletableFuture.supplyAsync(() -> {
                System.out.println("Executing first task for thenCompose on thread: " + Thread.currentThread().getName());
                sleep(500);
                return "First result";
            }).thenCompose(firstResult -> {
                System.out.println("Composing with second future on thread: " + Thread.currentThread().getName());
                return CompletableFuture.supplyAsync(() -> {
                    System.out.println("Executing second task from thenCompose on thread: " + Thread.currentThread().getName());
                    sleep(500);
                    return firstResult + " -> Second result";
                });
            });
            
            // Wait for the futures to complete
            System.out.println("Chained result: " + chainedFuture.get());
            System.out.println("thenCompose result: " + thenComposeFuture.get());
            
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