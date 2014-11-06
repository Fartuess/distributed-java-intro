package exercise1.equipment;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class Paints {
    private int available = 3;

    private final ReentrantLock myLock = new ReentrantLock();
    private final Condition condition = myLock.newCondition();

    public void takePaint() throws InterruptedException {
        myLock.lock();
        try {
            if (available == 0) {
                //throw new IllegalStateException("There are no more paints!");
                condition.await();
            }
            available -= 1;
        }
        finally {
            myLock.unlock();
        }
    }

    public void returnPaint() {
        myLock.lock();
        condition.signal();
        available += 1;
        myLock.unlock();
    }
}
