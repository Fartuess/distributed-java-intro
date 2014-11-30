package AdamBeczynski;

import java.util.Random;

/**
 * Created by CLEVO on 2014-11-10.
 *
 * Donor runnable.
 */
public class Donor implements Runnable
{
    public String name;
    private Item myItem;

    public Donor(String name)
    {
        this.name = name;
        myItem = new Item("Item_" + name);
    }

    //Donor register his item.
    private void RegisterItem(){

        //Helper value containing about success or failure of item registration.
        Chairman.ItemRegistrationStatus helper = Chairman.ItemRegistrationStatus.OTHER_FAILURE;

        //Donor waits a while before he try to register his item. Time window changed to make registration possible.
        Random random = new Random();
        try {
            Thread.sleep(random.nextInt(25000)/* + 1000*/);
        }
        catch (InterruptedException e)  {}

        //Donor keeps trying to register his item.
        do {
            synchronized (AuctionHouse.marketManager.auctionHouseState) {
                if (AuctionHouse.marketManager.auctionHouseState == MarketManager.AuctionHouseState.CLOSED) return; //Market is closed. There is no point in trying to donate an item.
            }

            //Donor tries to register his item and retrieve information about success.
            helper = AuctionHouse.chairman.addItem(myItem);

            if (helper == Chairman.ItemRegistrationStatus.QUEUE_FULL)
            {
                //Waits 5 seconds before trying donate item again.
                try {
                    Thread.sleep(5000);
                }
                catch (InterruptedException e)  {}
            }
            else
            {
                //if succeded donating item - removes it from himself and waiting for auction house to close.
                if (helper == Chairman.ItemRegistrationStatus.SUCCESS) {

                    myItem = null;

                    try {
                        synchronized (this) {
                            wait();
                        }
                        return;
                    } catch (InterruptedException e) {}
                }
            }
        } while (helper == Chairman.ItemRegistrationStatus.QUEUE_FULL);
    }

    //Entrance point for donor thread.
    @Override
    public void run() {

        if(!AuctionHouse.marketManager.RegisterDonor(this)) return; //Donor failed registration and ends his thread.

        //Waits for auction house to open.
        try {
            synchronized (this) {
                wait();
            }
        } catch (InterruptedException e) {}

        //Donor tries registering his item.
        RegisterItem();

        //Donor leaving auction house.
        System.out.println(name + " says good bye");
    }
}
