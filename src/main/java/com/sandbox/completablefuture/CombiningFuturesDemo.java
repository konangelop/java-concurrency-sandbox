package com.sandbox.completablefuture;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.sandbox.completablefuture.CompletableFutureUtils.sleep;
import static com.sandbox.completablefuture.CompletableFutureUtils.shutdownAndAwaitTermination;

/**
 * This class demonstrates how to combine multiple CompletableFutures.
 * 
 * Example 4 from the CompletableFuture demos.
 */
public class CombiningFuturesDemo {

    /**
     * Main method that demonstrates combining multiple CompletableFutures.
     *
     * @param args command line arguments (not used)
     */
    public static void main(String[] args) {
        System.out.println("Starting Combining Futures demo");
        System.out.println("Main thread: " + Thread.currentThread().getName());

        // Create a custom executor for our async operations
        ExecutorService executor = Executors.newFixedThreadPool(4);

        try {
            System.out.println("\n4. Combining multiple futures");
            
            // 4.1 thenCombine - combine two independent futures
            CompletableFuture<Integer> future1 = CompletableFuture.supplyAsync(() -> {
                System.out.println("Executing future1 on thread: " + Thread.currentThread().getName());
                sleep(500);
                return 10;
            });
            
            CompletableFuture<Integer> future2 = CompletableFuture.supplyAsync(() -> {
                System.out.println("Executing future2 on thread: " + Thread.currentThread().getName());
                sleep(700);
                return 20;
            });
            
            CompletableFuture<String> combinedFuture = future1.thenCombine(future2, (result1, result2) -> {
                System.out.println("Combining results on thread: " + Thread.currentThread().getName());
                return "Combined result: " + (result1 + result2);
            });
            
            // 4.2 allOf - wait for all futures to complete
            CompletableFuture<String> future3 = CompletableFuture.supplyAsync(() -> {
                System.out.println("Executing future3 on thread: " + Thread.currentThread().getName());
                sleep(600);
                return "Result from future3";
            });
            
            CompletableFuture<String> future4 = CompletableFuture.supplyAsync(() -> {
                System.out.println("Executing future4 on thread: " + Thread.currentThread().getName());
                sleep(800);
                return "Result from future4";
            });
            
            CompletableFuture<String> future5 = CompletableFuture.supplyAsync(() -> {
                System.out.println("Executing future5 on thread: " + Thread.currentThread().getName());
                sleep(400);
                return "Result from future5";
            });
            
            CompletableFuture<Void> allFutures = CompletableFuture.allOf(future3, future4, future5);
            
            // Wait for all futures to complete and then collect their results
            CompletableFuture<List<String>> allResultsFuture = allFutures.thenApply(v -> {
                System.out.println("All futures completed, collecting results on thread: " + Thread.currentThread().getName());
                return Stream.of(future3, future4, future5)
                        .map(CompletableFuture::join) // join() is similar to get() but doesn't throw checked exceptions
                        .collect(Collectors.toList());
            });
            
            // 4.3 anyOf - wait for any future to complete
            CompletableFuture<Object> anyFuture = CompletableFuture.anyOf(
                    CompletableFuture.supplyAsync(() -> {
                        System.out.println("Executing slow task on thread: " + Thread.currentThread().getName());
                        sleep(1000);
                        return "Slow task result";
                    }),
                    CompletableFuture.supplyAsync(() -> {
                        System.out.println("Executing medium task on thread: " + Thread.currentThread().getName());
                        sleep(500);
                        return "Medium task result";
                    }),
                    CompletableFuture.supplyAsync(() -> {
                        System.out.println("Executing fast task on thread: " + Thread.currentThread().getName());
                        sleep(200);
                        return "Fast task result";
                    })
            );
            
            // Wait for the futures to complete
            System.out.println("thenCombine result: " + combinedFuture.get());
            System.out.println("allOf results: " + allResultsFuture.get());
            System.out.println("anyOf result: " + anyFuture.get());
            
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