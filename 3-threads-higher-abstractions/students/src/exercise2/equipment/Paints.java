package exercise2.equipment;

import java.util.concurrent.ArrayBlockingQueue;

public class Paints {

    private ArrayBlockingQueue<String> paints = new ArrayBlockingQueue<String>(3);

    public Paints()
    {
        paints.add("RED");
        paints.add("GREEN");
        paints.add("BLUE");
    }

    public String takePaint() throws InterruptedException {
        return paints.take();
    }

    public void returnPaint(String paint) {
        try {
            paints.put(paint);
        }
        catch (InterruptedException e)
        {

        }
    }
}
