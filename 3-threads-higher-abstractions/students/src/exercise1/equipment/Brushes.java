package exercise1.equipment;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class Brushes {
    private int available = 3;

    private final ReentrantLock myLock = new ReentrantLock();
    private final Condition condition = myLock.newCondition();

    public void takeBrush() throws InterruptedException {
        myLock.lock();
        try
        {
            //throw new IllegalStateException("There are no more brushes!");
            if (available == 0) condition.await();
            available -= 1;
        }
        finally {
            myLock.unlock();
        }
    }

    public void returnBrush() {
        myLock.lock();
        available += 1;
        condition.signal();
        myLock.unlock();
    }
}
