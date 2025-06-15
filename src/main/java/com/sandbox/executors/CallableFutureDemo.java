package com.sandbox.executors;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import java.util.Random;

/**
 * This class demonstrates how to use Callables and Futures in Java's concurrency framework.
 *
 * Unlike Runnable, which doesn't return a result and cannot throw checked exceptions,
 * Callable can return a value and can throw checked exceptions. Future represents the
 * result of an asynchronous computation and provides methods to check if the computation
 * is complete, wait for its completion, and retrieve the result.
 *
 * This demo shows:
 * 1. Basic usage of Callable vs Runnable
 * 2. Submitting a Callable to an ExecutorService and getting a Future
 * 3. Getting results from a Future (with and without timeouts)
 * 4. Handling exceptions from Callables
 * 5. Cancelling a Future
 * 6. Using invokeAll() and invokeAny() methods
 * 7. Using CompletableFuture for advanced asynchronous operations
 */
public class CallableFutureDemo {

    /**
     * Main method that demonstrates the use of Callables and Futures.
     *
     * @param args command line arguments (not used)
     */
    public static void main(String[] args) {
        System.out.println("Starting Callable and Future demo");

        // Create a fixed thread pool with 4 threads
        ExecutorService executor = Executors.newFixedThreadPool(4);

        try {
            System.out.println("Main thread: " + Thread.currentThread().getName());

            // 1. Basic Callable that returns a result
            System.out.println("\n1. Basic Callable that returns a result");
            Callable<Integer> basicCallable = () -> {
                System.out.println("Executing basic callable on thread: " + Thread.currentThread().getName());
                Thread.sleep(1000);
                return 42; // Return a value
            };

            Future<Integer> future = executor.submit(basicCallable);
            System.out.println("Callable submitted, continuing with other work...");

            // Do some other work while the callable is executing
            Thread.sleep(500);
            System.out.println("Main thread doing other work while waiting for result");

            // Get the result (blocks until the callable completes)
            Integer result = future.get();
            System.out.println("Result from callable: " + result);

            // 2. Callable with timeout
            System.out.println("\n2. Callable with timeout");
            Callable<String> longRunningCallable = () -> {
                System.out.println("Executing long-running callable on thread: " + Thread.currentThread().getName());
                Thread.sleep(3000); // This takes longer than our timeout
                return "This result will not be retrieved due to timeout";
            };

            Future<String> timeoutFuture = executor.submit(longRunningCallable);

            try {
                // Try to get the result with a timeout of 1 second
                String timeoutResult = timeoutFuture.get(1, TimeUnit.SECONDS);
                System.out.println("Result: " + timeoutResult); // This won't execute
            } catch (TimeoutException e) {
                System.out.println("Timeout occurred while waiting for result");
                // Cancel the task since we're no longer interested in the result
                timeoutFuture.cancel(true);
                System.out.println("Task cancelled: " + timeoutFuture.isCancelled());
            }

            // 3. Handling exceptions from Callables
            System.out.println("\n3. Handling exceptions from Callables");
            Callable<Integer> exceptionCallable = () -> {
                System.out.println("Executing callable that throws exception on thread: " + Thread.currentThread().getName());
                Thread.sleep(500);
                throw new IllegalStateException("Deliberate exception from callable");
            };

            Future<Integer> exceptionFuture = executor.submit(exceptionCallable);

            try {
                Integer exceptionResult = exceptionFuture.get();
                System.out.println("Result: " + exceptionResult); // This won't execute
            } catch (ExecutionException e) {
                System.out.println("Exception from callable: " + e.getCause().getMessage());
            }

            // 4. Cancelling a Future
            System.out.println("\n4. Cancelling a Future");
            Callable<String> cancellableCallable = () -> {
                System.out.println("Executing cancellable callable on thread: " + Thread.currentThread().getName());
                try {
                    for (int i = 0; i < 10; i++) {
                        // Check for interruption
                        if (Thread.currentThread().isInterrupted()) {
                            System.out.println("Callable was interrupted");
                            throw new InterruptedException("Callable interrupted");
                        }
                        Thread.sleep(500);
                        System.out.println("Cancellable callable progress: " + (i + 1) * 10 + "%");
                    }
                    return "Cancellable callable completed successfully";
                } catch (InterruptedException e) {
                    System.out.println("Cancellable callable was interrupted");
                    throw e; // Re-throw to signal the interruption
                }
            };

            Future<String> cancellableFuture = executor.submit(cancellableCallable);

            // Let it run for a bit
            Thread.sleep(1500);

            // Cancel the task
            boolean wasCancelled = cancellableFuture.cancel(true); // true means interrupt if running
            System.out.println("Task was cancelled: " + wasCancelled);
            System.out.println("Task is cancelled: " + cancellableFuture.isCancelled());
            System.out.println("Task is done: " + cancellableFuture.isDone());

            // 5. Using invokeAll to execute multiple Callables
            System.out.println("\n5. Using invokeAll to execute multiple Callables");
            List<Callable<Integer>> callableList = new ArrayList<>();

            // Create 5 callables that return their index
            for (int i = 0; i < 5; i++) {
                final int index = i;
                callableList.add(() -> {
                    System.out.println("Callable " + index + " executing on thread: " + Thread.currentThread().getName());
                    Thread.sleep(1000);
                    return index * 10;
                });
            }

            // Execute all callables and get a list of futures
            List<Future<Integer>> futureList = executor.invokeAll(callableList);
            System.out.println("All callables have been submitted via invokeAll");

            // Get results from all futures
            for (int i = 0; i < futureList.size(); i++) {
                Integer invokeAllResult = futureList.get(i).get();
                System.out.println("Result from callable " + i + ": " + invokeAllResult);
            }

            // 6. Using invokeAny to execute multiple Callables and get the first result
            System.out.println("\n6. Using invokeAny to execute multiple Callables");
            List<Callable<String>> competingCallables = new ArrayList<>();

            // Create callables with different execution times
            Random random = new Random();
            for (int i = 0; i < 3; i++) {
                final int index = i;
                final int delay = 500 + random.nextInt(2000); // Random delay between 500-2500ms

                competingCallables.add(() -> {
                    System.out.println("Competing callable " + index + " starting with delay " + delay + "ms");
                    Thread.sleep(delay);
                    System.out.println("Competing callable " + index + " completed");
                    return "Result from callable " + index + " (delay: " + delay + "ms)";
                });
            }

            // Execute all callables and get the result from the first one to complete
            String firstResult = executor.invokeAny(competingCallables);
            System.out.println("First result from invokeAny: " + firstResult);

            // 7. Using CompletableFuture for advanced asynchronous operations
            System.out.println("\n7. Using CompletableFuture for advanced asynchronous operations");

            // 7.1 Basic CompletableFuture with supplyAsync
            System.out.println("\n7.1 Basic CompletableFuture with supplyAsync");
            CompletableFuture<Integer> completableFuture = CompletableFuture.supplyAsync(() -> {
                System.out.println("Executing supplyAsync on thread: " + Thread.currentThread().getName());
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
                return 42;
            });

            // 7.2 Chaining operations with thenApply and thenAccept
            System.out.println("\n7.2 Chaining operations with thenApply and thenAccept");
            completableFuture
                .thenApply(value -> {
                    System.out.println("Applying transformation on thread: " + Thread.currentThread().getName());
                    return value * 2;
                })
                .thenAccept(value -> {
                    System.out.println("Accepting final value on thread: " + Thread.currentThread().getName());
                    System.out.println("Final value: " + value);
                });

            // 7.3 Exception handling with exceptionally
            System.out.println("\n7.3 Exception handling with exceptionally");
            CompletableFuture<Integer> failedFuture = CompletableFuture.supplyAsync(() -> {
                System.out.println("Executing failing task on thread: " + Thread.currentThread().getName());
                throw new RuntimeException("Deliberate exception in CompletableFuture");
            });

            failedFuture
                .thenApply(value -> value * 2)
                .exceptionally(ex -> {
                    System.out.println("Handling exception on thread: " + Thread.currentThread().getName());
                    System.out.println("Exception message: " + ex.getCause().getMessage());
                    return -1; // Fallback value
                })
                .thenAccept(value -> System.out.println("Result after exception handling: " + value));

            // 7.4 Combining multiple CompletableFutures with thenCombine
            System.out.println("\n7.4 Combining multiple CompletableFutures with thenCombine");
            CompletableFuture<Integer> future1 = CompletableFuture.supplyAsync(() -> {
                System.out.println("Executing first task on thread: " + Thread.currentThread().getName());
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
                return 10;
            });

            CompletableFuture<Integer> future2 = CompletableFuture.supplyAsync(() -> {
                System.out.println("Executing second task on thread: " + Thread.currentThread().getName());
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
                return 20;
            });

            future1.thenCombine(future2, (result1, result2) -> {
                System.out.println("Combining results on thread: " + Thread.currentThread().getName());
                return result1 + result2;
            }).thenAccept(combinedResult -> 
                System.out.println("Combined result: " + combinedResult)
            );

            // Wait for all CompletableFuture operations to complete
            // This is necessary because CompletableFuture operations are non-blocking
            Thread.sleep(3000);
            System.out.println("All CompletableFuture operations completed");

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
