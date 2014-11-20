package AdamBeczynski;

import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Hello world!
 *
 */
public class App
{
    public static void main( String[] args ) throws InterruptedException
    {
        //System.out.println( "Hello World!" );
        ExecutorService auctionHouseExecutorService = Executors.newCachedThreadPool();
        //ArrayList<Donor> donorsHelper = new ArrayList<Donor>();
        //System.out.println("starting phase 1");
        auctionHouseExecutorService.execute(AuctionHouse.marketManager);
        //System.out.println("starting phase 2");
        auctionHouseExecutorService.execute(AuctionHouse.chairman);
        //System.out.println("starting phase 3");
        for(int i = 0; i < 12; i++)
        {
            auctionHouseExecutorService.execute(new Donor("Donor " + i));
        }
        //System.out.println("starting phase 4");
        for(int i = 0; i < 1; i++)
        {
            auctionHouseExecutorService.execute(new Recipient("Recipient " + i));
        }
    }
}
