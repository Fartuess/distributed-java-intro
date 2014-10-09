package exercise4;

public class MyRunnable implements Runnable {
    @Override
    public void run() {
        for( int i = 0; i < 10; i++)
        {
            System.out.println(Thread.currentThread().getName() + " " + Integer.toString(i));
            try {
                Thread.currentThread().sleep(10);
            }
            catch (InterruptedException exception)
            {

            }
        }
        /*
        try {
            TimeUnit.SECONDS.sleep(5);
        }
        catch (InterruptedException exception)
        {

        }
        System.out.println("FINISHED");
        */
    }
}