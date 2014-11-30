package AdamBeczynski;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Application entrance point
 *
 */
public class App
{
    public static void main( String[] args ) throws InterruptedException
    {
        //ThreadPool for application
        ExecutorService auctionHouseExecutorService = Executors.newCachedThreadPool();
        //Single Market Manager runnable object
        auctionHouseExecutorService.execute(AuctionHouse.marketManager);
        //Single Chairman runnable object
        auctionHouseExecutorService.execute(AuctionHouse.chairman);
        //Many Donors runnable objects
        for(int i = 0; i < 50; i++)
        {
            auctionHouseExecutorService.execute(new Donor("Donor " + i));
        }
        //Many Recipient runnable objects
        for(int i = 0; i < 20; i++)
        {
            auctionHouseExecutorService.execute(new Recipient("Recipient " + i));
        }
        //Application finish after all threads finish.
        auctionHouseExecutorService.shutdown();
    }
}
