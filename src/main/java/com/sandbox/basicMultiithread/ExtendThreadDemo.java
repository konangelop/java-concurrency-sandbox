package com.sandbox.basicMultiithread;

public class ExtendThreadDemo {
    public static void main(String[] args) {
        FirstExtendedThread firstExtendedThread = new FirstExtendedThread();
        SecondExtendedThread secondExtendedThread = new SecondExtendedThread();

        firstExtendedThread.start();
        secondExtendedThread.start();
    }
}

class FirstExtendedThread extends Thread {
    @Override
    public void run() {
        for (int i = 0; i < 15; i++) {
            System.out.println("Hello from first thread " + i);
        }
    }
}

class SecondExtendedThread extends Thread {
    @Override
    public void run() {
        for (int i = 0; i < 15; i++) {
            System.out.println("Hello from second thread " + i);
        }
    }
}