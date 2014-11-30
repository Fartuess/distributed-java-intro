package AdamBeczynski;

import java.util.ArrayList;
import java.util.Random;

/**
 * Created by CLEVO on 2014-11-10.
 *
 * Recipient runnable.
 */
public class Recipient implements Runnable
{
    public String name;
    public ArrayList<Item> ownedItems = new ArrayList<Item>();
    private boolean isLastWinner = false;

    public Recipient(String name)
    {
        this.name = name;
    }

    //Recipient tries registering to auction house.
    private Chairman.RecipientAuctionRegistrationStatus RegisterToCurrentAuction(){
        return AuctionHouse.chairman.registerRecipient(this);
    }

    //Recipient adding item to his collection.
    public void AddOwnedItem(Item item)
    {
        ownedItems.add(item);
        System.out.println(name + " won " + item.name);
        isLastWinner = true;
    }

    //Entrance point for recipient thread.
    @Override
    public void run() {

        if(!AuctionHouse.marketManager.RegisterRecipient(this)) return; //Recipient failed to register and end his thread.

        //Recipient waits for market to open
        try {
            synchronized (this) {
                wait();
            }
        } catch (InterruptedException e) {}

        //Recipient tries to register to auctions until market closes.
        while (true)
        {
            //trick to make synchronization block shorter
            synchronized (AuctionHouse.marketManager.auctionHouseState)
            {
                if(AuctionHouse.marketManager.auctionHouseState != MarketManager.AuctionHouseState.OPENED)  break;
            }

            //Recipient waits up to 5 seconds before trying to register for current auction.
            //*Waits 5 to 15 seconds if he won last auction.
            Random random = new Random();
            try {
                if(isLastWinner)
                {
                    Thread.sleep(random.nextInt(10000) + 5000);
                }
                else {
                    Thread.sleep(random.nextInt(5000));
                }
            }
            catch (InterruptedException e)  {}

            //Recipient attempting registration.
            Chairman.RecipientAuctionRegistrationStatus status = RegisterToCurrentAuction();

            if (status == Chairman.RecipientAuctionRegistrationStatus.SUCCESS) {
                //Locking recipient, so he doesn't try to register while he is already registered.
                try {
                    synchronized (this) {
                        wait();
                    }
                }
                catch (InterruptedException e) {
                }
            }
        }

        //Recipient leaving auction house.
        String ownedItemsString = "";
        for (int i = 0; i < ownedItems.size(); i++) {
            ownedItemsString += " " + ownedItems.get(i).name;
        }
        System.out.println(name + " says good bye leaving with items" + ownedItemsString);
    }
}
