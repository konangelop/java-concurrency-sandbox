# Java Concurrency Basics

## Concurrency vs. Parallelism

**Concurrency** and **parallelism** are related concepts but have distinct meanings:

**Concurrency** refers to the ability of a system to handle multiple tasks in overlapping time periods. In a concurrent system, multiple tasks make progress during the same time period, but they might not be executing simultaneously at any given point in time. Concurrency is about dealing with multiple things at once (task switching).

**Parallelism** refers to the ability of a system to perform multiple tasks simultaneously. In a parallel system, multiple tasks are literally executing at the exact same time. Parallelism requires multiple processors or processor cores and is about doing multiple things at once.

Key differences:
- Concurrency is about structure (how a program is designed to handle multiple tasks)
- Parallelism is about execution (actually performing multiple operations simultaneously)
- Concurrency can be achieved on a single-core processor through time-slicing
- Parallelism requires multiple cores/processors
- Concurrency focuses on managing access to shared resources
- Parallelism focuses on maximizing computational throughput

## Processes and Threads

**Process**: A process is an independent program in execution with its own memory space. Each process has:
- Its own memory address space
- System resources allocated by the operating system
- At least one thread of execution (the main thread)
- Isolation from other processes (one process cannot directly access another process's memory)

**Thread**: A thread is the smallest unit of execution within a process. Threads within the same process:
- Share the same memory space and resources of their parent process
- Have their own call stack and program counter
- Can communicate with each other directly (through shared memory)
- Are lightweight compared to processes

The Java Virtual Machine (JVM) allows an application to have multiple threads of execution running concurrently. Java provides built-in support for multithreaded programming through the `Thread` class and the `Runnable` interface.

## Creating Threads in Java

There are several ways to create a thread in Java:

### 1. Extending the Thread Class

```java
public class MyThread extends Thread {
    public void run() {
        System.out.println("Thread is running");
    }

    public static void main(String[] args) {
        MyThread thread = new MyThread();
        thread.start(); // Starts the thread
    }
}
```

### 2. Implementing the Runnable Interface

#### 2.1 Using a Separate Class

```java
public class MyRunnable implements Runnable {
    public void run() {
        System.out.println("Thread is running");
    }

    public static void main(String[] args) {
        Thread thread = new Thread(new MyRunnable());
        thread.start(); // Starts the thread
    }
}
```

#### 2.2 Using Lambda Expressions (Java 8+)

```java
public class LambdaThreadDemo {
    public static void main(String[] args) {
        Thread thread = new Thread(() -> {
            System.out.println("Thread is running with lambda expression");
        });
        thread.start(); // Starts the thread
    }
}
```

This approach is more concise and is particularly useful for simple thread implementations. The lambda expression implements the Runnable interface's single abstract method (run).

## Thread Lifecycle

A thread in Java goes through various states during its lifecycle:

1. **New**: The thread has been created but not yet started.
2. **Runnable**: The thread is ready to run and waiting for CPU time.
3. **Blocked**: The thread is waiting for a monitor lock to enter a synchronized block/method.
4. **Waiting**: The thread is waiting indefinitely for another thread to perform a particular action.
5. **Timed Waiting**: The thread is waiting for another thread to perform an action for a specified time.
6. **Terminated**: The thread has completed execution or was terminated abnormally.

## Thread Synchronization

When multiple threads access shared resources, it can lead to race conditions. Java provides synchronization mechanisms to ensure thread safety:

### Synchronized Methods

```java
public synchronized void synchronizedMethod() {
    // Only one thread can execute this method at a time
}
```

### Synchronized Blocks

```java
public void synchronizedBlock() {
    synchronized(this) {
        // Only one thread can execute this block at a time
    }
}
```

## Thread Communication

Threads can communicate with each other using methods like `wait()`, `notify()`, and `notifyAll()`:

```java
public synchronized void produce() throws InterruptedException {
    while(isProduced) {
        wait(); // Wait until the consumer consumes
    }
    // Produce an item
    isProduced = true;
    notify(); // Notify the consumer
}

public synchronized void consume() throws InterruptedException {
    while(!isProduced) {
        wait(); // Wait until the producer produces
    }
    // Consume the item
    isProduced = false;
    notify(); // Notify the producer
}
```

## Conclusion

This document provides a basic introduction to Java concurrency concepts. For more advanced topics, refer to other documents in this directory.
