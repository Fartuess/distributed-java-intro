package exercise2;

public class MyThread extends Thread
{
    MyThread(String threadName)
    {
        super.setName(threadName);
    }

    @Override
    public void run() {
        //super.run();
        for( int i = 0; i < 10; i++)
        {
            System.out.println(getName() + " " + Integer.toString(i));
            try {
                sleep(10);
            }
            catch (InterruptedException exception)
            {

            }
        }
    }
}
