package com.sandbox.sequential;

/**
 * This class demonstrates sequential execution of tasks.
 * In sequential execution, tasks are performed one after another,
 * with each task waiting for the previous task to complete before starting.
 */
public class SequentialExecutionDemo {

    /**
     * Main method that demonstrates sequential execution of tasks.
     * 
     * @param args command line arguments (not used)
     */
    public static void main(String[] args) {
        System.out.println("Starting sequential execution demo");

        // Perform three tasks sequentially
        System.out.println("Starting Task 1");
        performTask(1);

        System.out.println("Starting Task 2");
        performTask(2);

        System.out.println("Starting Task 3");
        performTask(3);

        System.out.println("All tasks completed sequentially");
    }

    /**
     * Simulates performing a task by sleeping for a short time.
     * 
     * @param taskId the ID of the task being performed
     */
    private static void performTask(int taskId) {
        try {
            // Simulate task execution time
            Thread.sleep(1000);
            System.out.println("Task " + taskId + " completed");
        } catch (InterruptedException e) {
            System.err.println("Task " + taskId + " was interrupted: " + e.getMessage());
            Thread.currentThread().interrupt();
        }
    }
}
