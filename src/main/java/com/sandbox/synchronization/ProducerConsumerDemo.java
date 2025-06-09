package com.sandbox.synchronization;

import java.util.LinkedList;
import java.util.Queue;

/**
 * This class demonstrates the producer-consumer pattern using two threads.
 * One thread (producer) produces items and adds them to a shared buffer.
 * Another thread (consumer) consumes items from the shared buffer.
 * The threads use wait/notify for communication to ensure the producer waits
 * when the buffer is full and the consumer waits when the buffer is empty.
 */
public class ProducerConsumerDemo {
    // Shared buffer with a maximum capacity
    private static final Queue<Integer> buffer = new LinkedList<>();
    private static final int MAX_CAPACITY = 5;

    // Object used as a lock for synchronization
    private static final Object LOCK = new Object();

    // Flag to control when threads should stop
    private static boolean running = true;

    /**
     * Produces an item and adds it to the buffer.
     * 
     * @param itemCount the current item count
     * @return the updated item count
     * @throws InterruptedException if the thread is interrupted while waiting
     */
    private static int produce(int itemCount) throws InterruptedException {
        synchronized (LOCK) {
            // Wait if the buffer is full
            while (buffer.size() >= MAX_CAPACITY && running) {
                System.out.println("Producer: Buffer full, waiting...");
                LOCK.wait();
            }

            if (!running) return itemCount;

            // Produce an item and add it to the buffer
            int item = ++itemCount;
            buffer.add(item);
            System.out.println("Producer: Produced item " + item + ", buffer size: " + buffer.size());

            // Notify the consumer that an item is available
            LOCK.notify();

            // Sleep a bit to simulate production time
            Thread.sleep(100);

            return itemCount;
        }
    }

    /**
     * Consumes an item from the buffer.
     * 
     * @throws InterruptedException if the thread is interrupted while waiting
     * @return true if an item was consumed, false if the thread should exit
     */
    private static boolean consume() throws InterruptedException {
        synchronized (LOCK) {
            // Wait if the buffer is empty
            while (buffer.isEmpty() && running) {
                System.out.println("Consumer: Buffer empty, waiting...");
                LOCK.wait();
            }

            if (!running && buffer.isEmpty()) return false;

            // Consume an item from the buffer
            int item = buffer.poll();
            System.out.println("Consumer: Consumed item " + item + ", buffer size: " + buffer.size());

            // Notify the producer that there's space in the buffer
            LOCK.notify();

            // Sleep a bit to simulate consumption time
            Thread.sleep(200);

            return true;
        }
    }

    /**
     * Main method that creates and starts the producer and consumer threads.
     * 
     * @param args command line arguments (not used)
     * @throws InterruptedException if any thread is interrupted
     */
    public static void main(String[] args) throws InterruptedException {
        System.out.println("Starting ProducerConsumerDemo");

        // Create the producer thread
        Thread producerThread = new Thread(() -> {
            int itemCount = 0;

            try {
                while (running && itemCount < 200) { // Produce 20 items and then stop
                    itemCount = produce(itemCount);
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                System.err.println("Producer: Interrupted: " + e.getMessage());
            }

            System.out.println("Producer: Finished production");
        }, "ProducerThread");

        // Create the consumer thread
        Thread consumerThread = new Thread(() -> {
            try {
                while (running && consume()) {
                    // The consume method handles all the logic
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                System.err.println("Consumer: Interrupted: " + e.getMessage());
            }

            System.out.println("Consumer: Finished consumption");
        }, "ConsumerThread");

        // Start both threads
        producerThread.start();
        consumerThread.start();

        // Let the threads run for a while
        Thread.sleep(5000);

        // Signal the threads to stop
        synchronized (LOCK) {
            running = false;
            LOCK.notifyAll(); // Wake up any waiting threads
        }

        // Wait for both threads to complete
        producerThread.join();
        consumerThread.join();

        System.out.println("ProducerConsumerDemo completed");
    }
}
