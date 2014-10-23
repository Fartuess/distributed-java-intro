package exercise1;

import common.Counter;

public class SynchronizedCounter implements Counter {

    private long value;

    public SynchronizedCounter()
    {
        value = 0;
    }

    @Override
    public synchronized void increment() {
        value++;
    }

    @Override
    public long getValue() {
        return value;
    }
}
