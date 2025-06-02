# Thread Joining in Java

## Parent-Child Thread Relationship

In Java, when a thread creates and starts another thread, we can conceptually think of this as a parent-child relationship. The thread that creates and starts another thread is the "parent" thread, while the newly created thread is the "child" thread.

It's important to understand that despite this conceptual relationship:
- Each thread runs independently
- Child threads don't automatically terminate when the parent thread terminates
- The JVM continues running until all non-daemon threads have completed

In the `JoinThreadsDemo` class, the main thread is the parent thread, and it creates two child threads using lambda expressions.

## What Happens When Threads Are Joined

The `join()` method forces the current thread to wait until the thread on which it is called completes its execution. When we call `join()` on a thread, the following happens:

1. The calling thread (often the parent thread) pauses its execution
2. The calling thread waits until the joined thread completes
3. After the joined thread completes, the calling thread resumes execution

In our `JoinThreadsDemo` example:

```
// Join both threads - main thread will wait until both threads complete
firstThread.join();
System.out.println("First thread joined");

secondThread.join();
System.out.println("Second thread joined");

System.out.println("All threads have completed execution");
```

The main thread waits for `firstThread` to complete before printing "First thread joined", then waits for `secondThread` to complete before printing "Second thread joined", and finally prints "All threads have completed execution" after both child threads have finished.

This ensures a predictable order of completion and guarantees that all child threads have finished their work before the parent thread continues.

## What Happens If Threads Are Not Joined

If we don't join the threads before the parent thread reaches its end, the following can happen:

1. The parent thread will continue execution without waiting for child threads to complete
2. The parent thread may finish before the child threads complete their tasks
3. If the parent thread is the main thread, the program might not terminate immediately because the JVM waits for all non-daemon threads to complete
4. The output from different threads will be unpredictable and interleaved

Let's modify our example to demonstrate what happens without joining:

```
// Without join()
System.out.println("Starting threads");

// Start both threads
firstThread.start();
secondThread.start();

System.out.println("Threads started");
System.out.println("Main thread continues without waiting");
System.out.println("Main thread has completed execution");
```

In this scenario:
- The main thread will likely finish before the child threads
- The "Main thread has completed execution" message will appear before the child threads finish their work
- The output from the child threads will continue to appear after the main thread has "completed"
- The program will only terminate after all non-daemon threads have finished

## Why Join Threads?

There are several important reasons to join threads:

1. **Predictable Program Flow**: Joining threads allows you to control the order of execution and ensure certain operations complete before others begin.

2. **Resource Cleanup**: If child threads are using resources that need to be cleaned up by the parent thread, joining ensures the resources are not cleaned up while still in use.

3. **Data Consistency**: If the parent thread needs to process results from child threads, joining ensures all results are available before processing begins.

4. **Graceful Shutdown**: Joining threads allows for a more controlled and graceful application shutdown.

## Conclusion

Thread joining is a fundamental concept in multithreaded programming that allows for coordination between threads. By understanding when to join threads and what happens if you don't, you can write more predictable and robust multithreaded applications.

The `JoinThreadsDemo` class demonstrates proper thread joining, ensuring that the main thread waits for all child threads to complete before finishing execution. This pattern is essential for many real-world multithreaded applications where coordination between threads is necessary.
