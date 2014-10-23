package exercise3;

import common.Counter;

import java.util.concurrent.atomic.AtomicLong;

public class AtomicCounter implements Counter {

    private AtomicLong value = new AtomicLong();

    public AtomicCounter()
    {
        value.set(0);
    }

    @Override
    public void increment() {
        value.incrementAndGet();
    }

    @Override
    public long getValue() {
        return value.get();
    }
}
