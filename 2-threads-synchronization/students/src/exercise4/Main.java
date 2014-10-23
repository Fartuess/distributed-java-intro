package exercise4;

import common.Counter;
import exercise1.SynchronizedCounter;
import exercise2.LockingCounter;
import exercise3.AtomicCounter;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static common.CountingRunner.numberOfIterations;
import static common.CountingRunner.numberOfThreads;

public class Main {

    public static void main(String[] args) throws InterruptedException {
        System.out.println("Synchronized Counter:");
        Counter counter = new SynchronizedCounter();//null; // TODO: Provide counter implementation
        execute(counter);
        System.out.println("Locking Counter:");
        counter = new LockingCounter();//null; // TODO: Provide counter implementation
        execute(counter);
        System.out.println("Atomic Counter:");
        counter = new AtomicCounter();//null; // TODO: Provide counter implementation
        execute(counter);
    }

    private static void execute(Counter counter) throws InterruptedException {
        ExecutorService executors = Executors.newCachedThreadPool();
        for (int i = 0; i < numberOfThreads; ++i) {
            executors.execute(new EvenCheckingTask(counter, numberOfIterations));
        }
        executors.shutdown();
        executors.awaitTermination(30, TimeUnit.SECONDS);
        System.out.println("Actual: " + counter.getValue() + ", Expected: " + (2 * numberOfThreads * numberOfIterations));
    }
}
