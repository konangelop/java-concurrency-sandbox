package com.sandbox.completablefuture;

import java.util.Random;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static com.sandbox.completablefuture.CompletableFutureUtils.sleep;
import static com.sandbox.completablefuture.CompletableFutureUtils.shutdownAndAwaitTermination;

/**
 * This class demonstrates a real-world example of using CompletableFutures
 * for parallel API calls with fallback and timeout.
 * 
 * Example 8 from the CompletableFuture demos.
 */
public class RealWorldExampleDemo {

    /**
     * Main method that demonstrates a real-world example of using CompletableFutures.
     *
     * @param args command line arguments (not used)
     */
    public static void main(String[] args) {
        System.out.println("Starting Real-World Example demo");
        System.out.println("Main thread: " + Thread.currentThread().getName());

        // Create a custom executor for our async operations
        ExecutorService executor = Executors.newFixedThreadPool(4);

        try {
            System.out.println("\n8. Real-world example: Parallel API calls with fallback and timeout");
            
            // Simulate multiple API calls with different response times
            CompletableFuture<String> fastApiCall = CompletableFuture.supplyAsync(() -> {
                System.out.println("Calling fast API on thread: " + Thread.currentThread().getName());
                sleep(300);
                if (new Random().nextInt(10) > 8) { // 10% chance of failure
                    throw new RuntimeException("Fast API failed");
                }
                return "Fast API response";
            }).completeOnTimeout("Fast API timed out", 500, TimeUnit.MILLISECONDS);
            
            CompletableFuture<String> reliableApiCall = CompletableFuture.supplyAsync(() -> {
                System.out.println("Calling reliable API on thread: " + Thread.currentThread().getName());
                sleep(800);
                return "Reliable API response";
            }).completeOnTimeout("Reliable API timed out", 1000, TimeUnit.MILLISECONDS);
            
            CompletableFuture<String> slowApiCall = CompletableFuture.supplyAsync(() -> {
                System.out.println("Calling slow API on thread: " + Thread.currentThread().getName());
                sleep(1500);
                return "Slow API response";
            }).completeOnTimeout("Slow API timed out", 1000, TimeUnit.MILLISECONDS);
            
            // Try the fast API first, fall back to reliable API, then to slow API
            CompletableFuture<String> apiResponse = fastApiCall
                    .exceptionally(ex -> {
                        System.out.println("Fast API failed, falling back to reliable API");
                        return null; // This will trigger the next exceptionally block
                    })
                    .thenCompose(result -> {
                        if (result != null) {
                            return CompletableFuture.completedFuture(result);
                        }
                        return reliableApiCall;
                    })
                    .exceptionally(ex -> {
                        System.out.println("Reliable API failed, falling back to slow API");
                        return null;
                    })
                    .thenCompose(result -> {
                        if (result != null) {
                            return CompletableFuture.completedFuture(result);
                        }
                        return slowApiCall;
                    })
                    .exceptionally(ex -> "All APIs failed or timed out");
            
            System.out.println("Final API response: " + apiResponse.get());
            
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