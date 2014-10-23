package exercise2;

import common.Counter;

import java.util.concurrent.locks.ReentrantLock;

public class LockingCounter implements Counter {

    private long value;
    private final ReentrantLock myLock = new ReentrantLock();

    public LockingCounter()
    {
        value = 0;
    }

    @Override
    public void increment() {
        myLock.lock();
        try {
            value++;
        }
        finally {
            myLock.unlock();
        }
    }

    @Override
    public long getValue() {
        return value;
    }
}
