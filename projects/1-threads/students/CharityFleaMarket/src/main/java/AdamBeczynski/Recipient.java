package AdamBeczynski;

import java.util.ArrayList;

/**
 * Created by CLEVO on 2014-11-10.
 */
public class Recipient implements Runnable
{
    public String name;
    boolean isRegistedToCurrentAuction = false;
    public ArrayList<Item> ownedItems = new ArrayList<Item>();

    public Recipient(String name)
    {
        this.name = name;
    }
    public Chairman.RecipientAuctionRegistrationStatus RegisterToCurrentAuction(){
        return AuctionHouse.chairman.registerRecipient(this);
    }

    public void AddOwnedItem(Item item)
    {
        ownedItems.add(item);
        System.out.println(name + " won " + item.name);
    }

    @Override
    public void run() {
        AuctionHouse.marketManager.RegisterRecipient(this);
        System.out.println("Recipient ready for your orders!");
        try {
            synchronized (this) {
                wait();
            }
        } catch (InterruptedException e) {}
        System.out.println("Recipient ready for registering action!");
        while (AuctionHouse.marketManager.isMarketOpened)
        {
            try {
                Thread.sleep(5000);
            }
            catch (InterruptedException e)  {}
            System.out.println("Recipient attempting registration!");
            Chairman.RecipientAuctionRegistrationStatus status = RegisterToCurrentAuction();
            if (status == Chairman.RecipientAuctionRegistrationStatus.SUCCESS) {
                System.out.println("Recipient Locked!");
                try {
                    synchronized (this) {
                        wait();
                    }
                }
                catch (InterruptedException e) {
                }
                System.out.println("Recipient Unlocked!");
            }
        }
        try {
            synchronized (this) {
                wait();
            }
        } catch (InterruptedException e) {}
        String ownedItemsString = "";
        for (int i = 0; i < ownedItems.size(); i++) {
            ownedItemsString += " " + ownedItems.get(i).name;
        }
        System.out.println(name + " says good bye leaving with items" + ownedItemsString);
    }
}
