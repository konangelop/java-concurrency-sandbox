package com.sandbox.completablefuture;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static com.sandbox.completablefuture.CompletableFutureUtils.sleep;
import static com.sandbox.completablefuture.CompletableFutureUtils.shutdownAndAwaitTermination;

/**
 * This class demonstrates timing control in CompletableFutures.
 * 
 * Example 6 from the CompletableFuture demos.
 */
public class TimingControlDemo {

    /**
     * Main method that demonstrates timing control in CompletableFutures.
     *
     * @param args command line arguments (not used)
     */
    public static void main(String[] args) {
        System.out.println("Starting Timing Control demo");
        System.out.println("Main thread: " + Thread.currentThread().getName());

        // Create a custom executor for our async operations
        ExecutorService executor = Executors.newFixedThreadPool(4);

        try {
            System.out.println("\n6. Timing control");
            
            // 6.1 orTimeout - complete with TimeoutException if not completed within timeout
            CompletableFuture<String> timeoutFuture = CompletableFuture.supplyAsync(() -> {
                System.out.println("Executing slow task for timeout on thread: " + Thread.currentThread().getName());
                sleep(2000); // This will exceed our timeout
                return "This result will never be returned due to timeout";
            }).orTimeout(1, TimeUnit.SECONDS);
            
            // 6.2 completeOnTimeout - complete with a default value if not completed within timeout
            CompletableFuture<String> completeOnTimeoutFuture = CompletableFuture.supplyAsync(() -> {
                System.out.println("Executing slow task for completeOnTimeout on thread: " + Thread.currentThread().getName());
                sleep(2000); // This will exceed our timeout
                return "This result will never be returned due to timeout";
            }).completeOnTimeout("Default timeout result", 1, TimeUnit.SECONDS);
            
            // Wait for the futures to complete
            try {
                System.out.println("orTimeout result: " + timeoutFuture.get());
            } catch (ExecutionException e) {
                System.out.println("orTimeout threw exception: " + e.getCause().getMessage());
            }
            
            System.out.println("completeOnTimeout result: " + completeOnTimeoutFuture.get());
            
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