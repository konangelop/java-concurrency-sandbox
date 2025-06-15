# Callables and Futures in Java

This document provides a comprehensive explanation of Callables and Futures in Java's concurrency framework, including their purpose, usage patterns, and best practices.

## Table of Contents
- [Introduction](#introduction)
- [Callable Interface](#callable-interface)
- [Future Interface](#future-interface)
- [Callable vs Runnable](#callable-vs-runnable)
- [Common Patterns and Use Cases](#common-patterns-and-use-cases)
- [CompletableFuture](#completablefuture)
- [Best Practices](#best-practices)
- [References](#references)

## Introduction

In Java's concurrency framework, `Callable` and `Future` are two key interfaces that enable asynchronous programming with results. While the `Runnable` interface has been part of Java since its early versions, it has a significant limitation: it cannot return a result or throw checked exceptions. The `Callable` interface, introduced in Java 5 along with the `Future` interface, addresses these limitations.

Together, these interfaces provide a powerful mechanism for executing tasks asynchronously and retrieving their results when they become available. This approach allows your application to continue execution without waiting for long-running operations to complete, improving responsiveness and resource utilization.

## Callable Interface

The `Callable` interface represents a task that returns a result and may throw an exception. It is defined in the `java.util.concurrent` package and has a single method:

```
public interface Callable<V> {
    V call() throws Exception;
}
```

### Key Characteristics

- **Generic Type**: The type parameter `V` specifies the type of the result returned by the `call()` method.
- **Return Value**: Unlike `Runnable.run()`, the `call()` method returns a value.
- **Exception Handling**: The `call()` method can throw checked exceptions, which are propagated to the caller.
- **Execution**: Callables are typically submitted to an `ExecutorService` using the `submit()` method, which returns a `Future` representing the pending result.

### Creating a Callable

There are several ways to create a `Callable`:

1. **Lambda Expression** (Java 8+):
   ```
   Callable<Integer> callable = () -> {
       // Perform some computation
       return 42;
   };
   ```

2. **Anonymous Class**:
   ```
   Callable<Integer> callable = new Callable<Integer>() {
       @Override
       public Integer call() throws Exception {
           // Perform some computation
           return 42;
       }
   };
   ```

3. **Method Reference** (Java 8+):
   ```
   public Integer compute() throws Exception {
       // Perform some computation
       return 42;
   }
   
   Callable<Integer> callable = this::compute;
   ```

## Future Interface

The `Future` interface represents the result of an asynchronous computation. It provides methods to check if the computation is complete, wait for its completion, and retrieve the result.

```
public interface Future<V> {
    boolean cancel(boolean mayInterruptIfRunning);
    boolean isCancelled();
    boolean isDone();
    V get() throws InterruptedException, ExecutionException;
    V get(long timeout, TimeUnit unit) 
        throws InterruptedException, ExecutionException, TimeoutException;
}
```

### Key Methods

1. **`cancel(boolean mayInterruptIfRunning)`**: Attempts to cancel the execution of the task. Returns `true` if the task was cancelled, `false` if it could not be cancelled (e.g., because it has already completed).
   - If `mayInterruptIfRunning` is `true`, the thread executing the task will be interrupted.
   - If `mayInterruptIfRunning` is `false`, the task will only be cancelled if it hasn't started execution yet.

2. **`isCancelled()`**: Returns `true` if the task was cancelled before it completed normally.

3. **`isDone()`**: Returns `true` if the task has completed (either normally, by throwing an exception, or by cancellation).

4. **`get()`**: Waits if necessary for the computation to complete, and then retrieves its result. This method blocks until the result is available.
   - Throws `InterruptedException` if the current thread was interrupted while waiting.
   - Throws `ExecutionException` if the computation threw an exception.
   - Throws `CancellationException` if the computation was cancelled.

5. **`get(long timeout, TimeUnit unit)`**: Similar to `get()`, but with a timeout. If the result is not available within the specified timeout, a `TimeoutException` is thrown.

### Obtaining a Future

The most common way to obtain a `Future` is by submitting a `Callable` to an `ExecutorService`:

```
ExecutorService executor = Executors.newFixedThreadPool(1);
Future<Integer> future = executor.submit(() -> {
    // Perform some computation
    return 42;
});
```

## Callable vs Runnable

While both `Callable` and `Runnable` represent tasks that can be executed by a thread, they have several key differences:

| Feature | Runnable | Callable |
|---------|----------|----------|
| **Return Value** | No (void) | Yes (generic type) |
| **Exception Handling** | Cannot throw checked exceptions | Can throw checked exceptions |
| **Execution Method** | `execute()` or `submit()` | `submit()`, `invokeAll()`, or `invokeAny()` |
| **Result Handling** | None | Via Future interface |
| **Introduction** | Java 1.0 | Java 5 |
| **Use Case** | Fire-and-forget tasks | Tasks that need to return results or throw exceptions |

### When to Use Callable vs Runnable

- Use **Runnable** when:
  - You don't need to return a result
  - You don't need to throw checked exceptions
  - You want to use the task with both legacy APIs (e.g., `Thread` constructor) and newer concurrency utilities

- Use **Callable** when:
  - You need to return a result from the task
  - You need to throw checked exceptions from the task
  - You need to use features like cancellation, timeout, or exception handling

## Common Patterns and Use Cases

### Basic Usage

The most basic pattern is to submit a `Callable` to an `ExecutorService` and then retrieve the result from the returned `Future`:

```
ExecutorService executor = Executors.newFixedThreadPool(1);
try {
    // Create and submit a Callable
    Future<Integer> future = executor.submit(() -> {
        // Simulate a long-running computation
        Thread.sleep(1000);
        return 42;
    });
    
    // Do other work while the Callable is executing
    System.out.println("Doing other work...");
    
    // Get the result (blocks until the result is available)
    Integer result = future.get();
    System.out.println("Result: " + result);
} finally {
    executor.shutdown();
}
```

### Handling Timeouts

To avoid blocking indefinitely, you can use the timed version of `get()`:

```
try {
    // Try to get the result with a timeout of 2 seconds
    Integer result = future.get(2, TimeUnit.SECONDS);
    System.out.println("Result: " + result);
} catch (TimeoutException e) {
    // Handle timeout
    System.out.println("Operation timed out");
    future.cancel(true); // Cancel the task
}
```

### Exception Handling

When a `Callable` throws an exception, it's wrapped in an `ExecutionException` and thrown by the `get()` method:

```
try {
    Future<Integer> future = executor.submit(() -> {
        if (someCondition) {
            throw new IOException("Something went wrong");
        }
        return 42;
    });
    
    Integer result = future.get();
    System.out.println("Result: " + result);
} catch (ExecutionException e) {
    // The original exception is available as the cause
    Throwable cause = e.getCause();
    System.err.println("Task failed: " + cause.getMessage());
    
    // Handle specific exceptions
    if (cause instanceof IOException) {
        // Handle IO exception
    } else {
        // Handle other exceptions
    }
}
```

### Cancellation

You can cancel a task that hasn't completed yet:

```
Future<String> future = executor.submit(() -> {
    try {
        for (int i = 0; i < 10; i++) {
            // Check for interruption
            if (Thread.currentThread().isInterrupted()) {
                throw new InterruptedException("Task cancelled");
            }
            Thread.sleep(500);
            System.out.println("Progress: " + (i + 1) * 10 + "%");
        }
        return "Task completed successfully";
    } catch (InterruptedException e) {
        // Propagate the interruption
        Thread.currentThread().interrupt();
        throw e;
    }
});

// Let it run for a bit
Thread.sleep(1500);

// Cancel the task
boolean wasCancelled = future.cancel(true); // true means interrupt if running
System.out.println("Task was cancelled: " + wasCancelled);
System.out.println("Task is cancelled: " + future.isCancelled());
System.out.println("Task is done: " + future.isDone());
```

### Executing Multiple Tasks

The `ExecutorService` interface provides methods for executing multiple `Callable` tasks:

1. **`invokeAll(Collection<? extends Callable<T>> tasks)`**: Executes all tasks and returns a list of `Future` objects in the same order.

```
List<Callable<Integer>> tasks = new ArrayList<>();
for (int i = 0; i < 5; i++) {
    final int index = i;
    tasks.add(() -> index * 10);
}

List<Future<Integer>> futures = executor.invokeAll(tasks);
for (int i = 0; i < futures.size(); i++) {
    Integer result = futures.get(i).get();
    System.out.println("Result from task " + i + ": " + result);
}
```

2. **`invokeAny(Collection<? extends Callable<T>> tasks)`**: Executes the tasks and returns the result of one that successfully completes (if any).

```
List<Callable<String>> tasks = new ArrayList<>();
tasks.add(() -> {
    Thread.sleep(2000);
    return "Result from task 1";
});
tasks.add(() -> {
    Thread.sleep(1000);
    return "Result from task 2";
});
tasks.add(() -> {
    Thread.sleep(3000);
    return "Result from task 3";
});

// This will return the result from task 2 (the fastest)
String result = executor.invokeAny(tasks);
System.out.println("First result: " + result);
```

## CompletableFuture

While `Future` provides a way to retrieve the result of an asynchronous computation, it has limitations:
- You cannot chain operations to be performed after the computation completes
- You cannot combine multiple Futures
- You cannot handle exceptions in a functional way

Java 8 introduced `CompletableFuture`, which extends `Future` and provides a more powerful and flexible API for asynchronous programming:

```
CompletableFuture<Integer> future = CompletableFuture.supplyAsync(() -> {
    // Perform some computation
    return 42;
});

future.thenApply(result -> result * 2)
      .thenAccept(result -> System.out.println("Result: " + result))
      .exceptionally(ex -> {
          System.err.println("Computation failed: " + ex.getMessage());
          return null;
      });
```

A full discussion of `CompletableFuture` is beyond the scope of this document, but it's worth exploring for more advanced asynchronous programming needs.

## Best Practices

1. **Always shut down executor services properly**:
   ```
   try {
       // Use the executor service
   } finally {
       executor.shutdown();
       try {
           if (!executor.awaitTermination(10, TimeUnit.SECONDS)) {
               executor.shutdownNow();
           }
       } catch (InterruptedException e) {
           executor.shutdownNow();
           Thread.currentThread().interrupt();
       }
   }
   ```

2. **Handle interruptions properly**:
   - When a task is cancelled with `future.cancel(true)`, the thread executing the task will be interrupted
   - Check for interruption in long-running tasks and respond appropriately
   - Preserve the interruption status by calling `Thread.currentThread().interrupt()`

3. **Use timeouts to avoid blocking indefinitely**:
   - Always consider using the timed version of `get()` to avoid blocking forever
   - Handle `TimeoutException` appropriately, typically by cancelling the task

4. **Handle exceptions properly**:
   - Remember that exceptions thrown by a `Callable` are wrapped in an `ExecutionException`
   - Extract and handle the original exception using `getCause()`

5. **Consider using CompletableFuture for complex asynchronous operations**:
   - When you need to chain operations or combine multiple asynchronous tasks
   - When you need more control over exception handling
   - When you need to specify the thread pool for different stages of the computation

6. **Be careful with resource management**:
   - Ensure resources are properly closed even if the task is cancelled
   - Use try-with-resources for resource management

7. **Avoid blocking operations in the main thread**:
   - Use `Future.get()` with a timeout or in a separate thread
   - Consider using non-blocking alternatives like `CompletableFuture.thenAccept()`

## References

### Official Documentation

- [Java SE Documentation: Callable](https://docs.oracle.com/en/java/javase/17/docs/api/java.base/java/util/concurrent/Callable.html) - Official Java documentation for the Callable interface
- [Java SE Documentation: Future](https://docs.oracle.com/en/java/javase/17/docs/api/java.base/java/util/concurrent/Future.html) - Official Java documentation for the Future interface
- [Java SE Documentation: ExecutorService](https://docs.oracle.com/en/java/javase/17/docs/api/java.base/java/util/concurrent/ExecutorService.html) - Documentation for the ExecutorService interface, including submit(), invokeAll(), and invokeAny() methods
- [Java SE Documentation: CompletableFuture](https://docs.oracle.com/en/java/javase/17/docs/api/java.base/java/util/concurrent/CompletableFuture.html) - Documentation for the CompletableFuture class

### Tutorials and Articles

- [Baeldung: Guide to java.util.concurrent.Future](https://www.baeldung.com/java-future) - Comprehensive guide to using Futures in Java
- [Oracle: Callable and Future Tutorial](https://docs.oracle.com/javase/tutorial/essential/concurrency/executors.html) - Oracle's tutorial on Callable and Future
- [Jakob Jenkov: Java Callable and Future](https://jenkov.com/tutorials/java-util-concurrent/callable-future.html) - Detailed explanation with examples
- [IBM Developer: Java theory and practice: Dealing with InterruptedException](https://www.ibm.com/developerworks/library/j-jtp05236/index.html) - Article on handling interruptions properly

### Books

- [Java Concurrency in Practice](https://jcip.net/) by Brian Goetz et al. - The definitive book on Java concurrency
- [Modern Java in Action](https://www.manning.com/books/modern-java-in-action) by Raoul-Gabriel Urma, Mario Fusco, and Alan Mycroft - Covers CompletableFuture and other modern Java concurrency features

### Example Code

See [CallableFutureDemo.java](../src/main/java/com/sandbox/executors/CallableFutureDemo.java) in our project for a complete example of how to use Callables and Futures.