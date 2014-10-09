package exercise3;

public class Main {

    public static void main(String[] args) {
        Thread threads[];
        threads = new Thread[4];
        for( int i = 0; i < 4; i++)
        {
            threads[i] = new Thread(new MyRunnable(), "Thread-" + Integer.toString(i));
        }
        /*
        for (int i = 0; i < 4; i++) {
            threads[i].start();
        }
        /*
        while(true) {
            boolean isAnybodyAlive = false;
            try {
                Thread.currentThread().sleep(10);
            }
            catch (InterruptedException exception)
            {

            }
            for (int i = 0; i < 4; i++) {
                isAnybodyAlive = isAnybodyAlive || threads[i].isAlive();
            }
            if( isAnybodyAlive == false) break;
        }
        */
        System.out.println("FINISHED");
        for (int i = 0; i < 4; i++) {
            threads[i].start();
        }
        for (int i = 0; i<4;i++)
        {
            try {
                threads[i].join();
            }
            catch (InterruptedException exception)
            {
                System.out.println("EXCEPTION");
            }
        }
    }
}
