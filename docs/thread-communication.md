# Thread Communication with wait() and notify()

In addition to synchronization for mutual exclusion, Java provides a mechanism for threads to communicate with each other using the `wait()`, `notify()`, and `notifyAll()` methods. These methods are defined in the `Object` class and can only be called from within a synchronized context (either a synchronized method or a synchronized block).

> **Additional Resource**: For more detailed information about thread signaling in Java, see Jakob Jenkov's excellent tutorial: [Java Thread Signaling](https://jenkov.com/tutorials/java-concurrency/thread-signaling.html)

## The wait/notify Mechanism

The wait/notify mechanism allows threads to coordinate their activities:

1. **wait()**: Causes the current thread to release the lock and wait until either:
   - Another thread calls `notify()` or `notifyAll()` on the same object
   - The thread is interrupted
   - A specified amount of time has passed (when using the timed version of `wait()`)

2. **notify()**: Wakes up a single thread that is waiting on the object's monitor. If multiple threads are waiting, one is chosen arbitrarily.

3. **notifyAll()**: Wakes up all threads that are waiting on the object's monitor.

## Example: WaitNotifyDemo

The `WaitNotifyDemo` class in our project demonstrates the wait/notify mechanism:

```java
public class WaitNotifyDemo {
    // Object used as a lock for synchronization
    private static final Object LOCK = new Object();

    // Message to be passed between threads
    private static String message = null;

    public static void main(String[] args) throws InterruptedException {
        System.out.println("Starting WaitNotifyDemo");

        // Create the waiting thread
        Thread waitingThread = new Thread(() -> {
            synchronized (LOCK) {
                System.out.println("Waiting Thread: Acquired the lock");

                try {
                    System.out.println("Waiting Thread: Going to wait...");
                    LOCK.wait(); // Release the lock and wait for notification
                    System.out.println("Waiting Thread: Woke up from wait state");
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    System.err.println("Waiting Thread: Interrupted while waiting: " + e.getMessage());
                }

                // Process the message after being notified
                System.out.println("Waiting Thread: Received message: " + message);
            }
            System.out.println("Waiting Thread: Finished execution");
        }, "WaitingThread");

        // Create the notifying thread
        Thread notifyingThread = new Thread(() -> {
            synchronized (LOCK) {
                System.out.println("Notifying Thread: Acquired the lock");

                // Set the message
                message = "Hello from the notifying thread!";

                // Notify the waiting thread
                System.out.println("Notifying Thread: Sending notification with message: " + message);
                LOCK.notify();

                // Note that the waiting thread won't wake up until this thread releases the lock
                System.out.println("Notifying Thread: Notification sent, about to release the lock");
            }

            System.out.println("Notifying Thread: Released the lock, waiting thread can now proceed");
        }, "NotifyingThread");

        // Start both threads
        waitingThread.start();
        notifyingThread.start();

        // Wait for both threads to complete
        waitingThread.join();
        notifyingThread.join();

        System.out.println("WaitNotifyDemo completed");
    }
}
```

## How wait/notify Works

In this example:

1. The `waitingThread` acquires the lock on the `LOCK` object and then calls `wait()`, which:
   - Releases the lock
   - Suspends the thread's execution
   - Moves the thread to the WAITING state

2. The `notifyingThread` then:
   - Acquires the lock on the `LOCK` object
   - Sets a message
   - Calls `notify()` to wake up the waiting thread
   - Completes its synchronized block, releasing the lock

3. Once the lock is released by the `notifyingThread`, the `waitingThread`:
   - Transitions from WAITING back to RUNNABLE
   - Re-acquires the lock
   - Continues execution from the point after the `wait()` call
   - Processes the message set by the notifying thread

## Thread State Transitions with wait/notify

The wait/notify mechanism introduces additional thread state transitions:

1. **RUNNABLE → WAITING**: When a thread calls `wait()`, it transitions from RUNNABLE to WAITING.

2. **WAITING → RUNNABLE**: When another thread calls `notify()` or `notifyAll()` and the waiting thread is selected to wake up, it transitions from WAITING to RUNNABLE (but may need to acquire the lock before continuing execution).

## Common Pitfalls with wait/notify

1. **Calling wait() or notify() without holding the lock**: This results in an `IllegalMonitorStateException`.

2. **Lost notifications**: If `notify()` is called before `wait()`, the notification is lost, and the waiting thread may wait indefinitely.

3. **Spurious wakeups**: A waiting thread may wake up without being notified. Always use `wait()` in a loop that checks a condition.

4. **Deadlocks**: Improper use of wait/notify can lead to deadlocks, where threads are waiting for each other indefinitely.

## Best Practices for wait/notify

1. **Always call wait() in a loop that checks a condition**:

```
synchronized (lock) {
    while (!condition) {
        lock.wait();
    }
    // Process when condition is true
}
```

2. **Prefer notifyAll() over notify()** unless you're certain that any waiting thread can process the notification.

3. **Document the condition being waited for** to make the code more maintainable.

4. **Consider higher-level concurrency utilities** from `java.util.concurrent` like `BlockingQueue`, `CountDownLatch`, or `Semaphore` instead of raw wait/notify.

## Alternative Signaling Mechanisms

While wait/notify is the traditional mechanism for thread signaling in Java, there are alternative approaches that may be more suitable in certain scenarios:

### Busy Wait

A busy wait involves continuously checking a condition in a loop:

```java
public class BusyWaitExample {
    private static boolean signal = false;

    public static void main(String[] args) throws InterruptedException {
        Thread waitingThread = new Thread(() -> {
            // Busy waiting
            while(!signal) {
                // Do nothing, just keep checking
            }
            System.out.println("Signal received, proceeding with execution");
        });

        waitingThread.start();

        Thread.sleep(1000); // Simulate some work

        // Set the signal and allow waiting thread to proceed
        signal = true;

        waitingThread.join();
    }
}
```

This approach is generally not recommended as it consumes CPU resources unnecessarily. However, it can be useful in very specific scenarios where the wait time is expected to be extremely short.

### Lock and Condition

The `java.util.concurrent.locks` package provides more flexible locking mechanisms:

```java
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class LockConditionExample {
    private final Lock lock = new ReentrantLock();
    private final Condition condition = lock.newCondition();
    private boolean signal = false;

    public void doWait() throws InterruptedException {
        lock.lock();
        try {
            while (!signal) {
                condition.await(); // Similar to wait()
            }
            // Process after signal
        } finally {
            lock.unlock();
        }
    }

    public void doNotify() {
        lock.lock();
        try {
            signal = true;
            condition.signal(); // Similar to notify()
        } finally {
            lock.unlock();
        }
    }
}
```

This approach offers more flexibility than traditional wait/notify, including the ability to create multiple conditions per lock.

### BlockingQueue

For producer-consumer scenarios, the `BlockingQueue` interface provides a thread-safe way to exchange data:

```java
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class BlockingQueueExample {
    public static void main(String[] args) {
        BlockingQueue<String> queue = new LinkedBlockingQueue<>(10);

        // Producer thread
        new Thread(() -> {
            try {
                queue.put("Message"); // Blocks if queue is full
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }).start();

        // Consumer thread
        new Thread(() -> {
            try {
                String message = queue.take(); // Blocks if queue is empty
                System.out.println("Received: " + message);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }).start();
    }
}
```

## Conclusion

The wait/notify mechanism provides a powerful way for threads to communicate and coordinate their activities. When used correctly, it allows threads to efficiently wait for specific conditions to be met without consuming CPU resources through busy-waiting.

In our `WaitNotifyDemo` example, we've seen how one thread can wait for a signal from another thread, and how data (the message) can be safely passed between threads using this mechanism in conjunction with proper synchronization.

As shown in the alternative approaches section, Java offers several mechanisms for thread communication beyond wait/notify. The choice of which mechanism to use depends on the specific requirements of your application, including performance considerations, flexibility needs, and the complexity of the coordination required.
