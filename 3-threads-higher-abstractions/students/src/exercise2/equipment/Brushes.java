package exercise2.equipment;

import java.util.concurrent.ArrayBlockingQueue;

public class Brushes {

    private ArrayBlockingQueue<String> brushes = new ArrayBlockingQueue<String>(3);

    public Brushes()
    {
        brushes.add("ROUND");
        brushes.add("SQUARE");
        brushes.add("TRIANGLE");
    }

    public String takeBrush() throws InterruptedException {
        return brushes.take();
    }

    public void returnBrush(String brush) {
        try {
            brushes.put(brush);
        }
        catch (InterruptedException e)
        {

        }
    }
}
