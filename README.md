# Java Concurrency Sandbox

This project serves as a practical demonstration of Java concurrency concepts.

## Project Overview

The Java Concurrency Sandbox is designed to provide hands-on examples of various concurrency patterns and techniques in Java. It aims to help developers understand and implement concurrent programming concepts effectively.

## Concurrency Concepts Covered

This sandbox will demonstrate various Java concurrency concepts including:

- Thread creation and management
- Synchronization mechanisms
- Locks and atomic operations
- Thread pools and executors
- CompletableFuture and asynchronous programming
- Concurrent collections
- Thread safety patterns
- Deadlock prevention and handling

## Project Structure

The project is organized as follows:

- `src/main/java/com/sandbox/` - Contains all the example code
  - `Main.java` - Entry point for running examples
- `docs/` - Contains markdown files with theory about Java concurrency concepts
  - `concurrency-basics.md` - Introduction to Java concurrency basics

## Prerequisites

- Java 23 (as specified in pom.xml)
- Maven

## Getting Started

1. Clone the repository:
   ```
   git clone https://github.com/yourusername/java-concurrency-sandbox.git
   ```

2. Build the project:
   ```
   mvn clean install
   ```

3. Run the examples:
   ```
   mvn exec:java -Dexec.mainClass="com.sandbox.Main"
   ```

## How to Use This Sandbox

Each concurrency concept is demonstrated in separate classes or packages. To explore a specific concept:

1. Navigate to the relevant class
2. Read the comments explaining the concept
3. Run the example to see it in action
4. Experiment by modifying the code to deepen your understanding

## Important Note on Compilation

This project uses Maven for build management. Always use Maven commands to compile and run the code:

```
# To compile the project
mvn compile

# To run a specific class
mvn exec:java -Dexec.mainClass="com.sandbox.executors.ScheduledThreadPoolDemo"
```

Avoid using direct `javac` commands (like `javac src\main\java\com\sandbox\executors\ScheduledThreadPoolDemo.java`) as this will create `.class` files in your source directories, which is not recommended. Maven automatically places compiled classes in the `target/classes` directory, keeping your source directories clean.

## References

This project is based on the concepts taught in the [Java Concurrency and Multithreading](https://www.youtube.com/watch?v=gvQGKRlgop) (Note: This URL may be incomplete, please verify the correct link) tutorial.

## Contributing

Contributions are welcome! If you'd like to add more examples or improve existing ones:

1. Fork the repository
2. Create a feature branch
3. Add your examples
4. Submit a pull request

## License

This project is available under the MIT License.
