package AdamBeczynski;

import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by CLEVO on 2014-11-10.
 *
 * Chairman runnable.
 */
public class Chairman implements Runnable {
    public volatile ArrayBlockingQueue<Item> itemQueue = new ArrayBlockingQueue<Item>(10);
    private volatile ArrayList<Recipient> currentAuctionRecipients = new ArrayList<Recipient>();

    private volatile AtomicBoolean isAuctionInProcess = new AtomicBoolean(false);
    private final ReentrantLock itemLock = new ReentrantLock();
    private final ReentrantLock recipientLock = new ReentrantLock();

    //Status describing state of success of item registration.
    public enum ItemRegistrationStatus
    {
        NO_AUCTION_IN_PROGRESS,
        QUEUE_FULL,
        SUCCESS,
        OTHER_FAILURE
    }

    //Status describing state of success of recipient registration.
    public enum RecipientAuctionRegistrationStatus
    {
        NO_AUCTION_IN_PROGRESS,
        LIST_FULL,
        SUCCESS,
        OTHER_FAILURE
    }

    //Method for adding item to auction queue.
    public synchronized ItemRegistrationStatus addItem(Item item)  //is synchronized needed?
    {
        //If market is not opened it is not possible to register item.
        synchronized (AuctionHouse.marketManager.auctionHouseState) {
            if (AuctionHouse.marketManager.auctionHouseState != MarketManager.AuctionHouseState.OPENED) {
                return ItemRegistrationStatus.NO_AUCTION_IN_PROGRESS;
            }
        }

        //Trying to add item to queue.
        ItemRegistrationStatus isSuccess = ItemRegistrationStatus.OTHER_FAILURE;
        itemLock.lock();
        try {
            if (itemQueue.size() >= 10)
            {
                isSuccess = ItemRegistrationStatus.QUEUE_FULL;  //Item registration failed - queue full.
            }
            else
            {
                itemQueue.add(item);    //No synchronization needed as it is blocking queue.
                isSuccess = ItemRegistrationStatus.SUCCESS; //Item added successfully.
            }
        }
        finally {
            itemLock.unlock();
            return isSuccess;
        }
    }

    //Method for registering recipients for current auction.
    public synchronized RecipientAuctionRegistrationStatus registerRecipient(Recipient recipient) //is synchronized needed?
    {
        if(!isAuctionInProcess.get()) return RecipientAuctionRegistrationStatus.NO_AUCTION_IN_PROGRESS; //Currently there are no auction i progress.

        synchronized (currentAuctionRecipients) {
            if (currentAuctionRecipients.size() >= 10)  return RecipientAuctionRegistrationStatus.LIST_FULL;    //List for current auction recipients is full. No more can be registered
        }

        //Trying to register recipient for current auction.
        RecipientAuctionRegistrationStatus isSuccess = RecipientAuctionRegistrationStatus.OTHER_FAILURE;
        recipientLock.lock();
        try {
            synchronized (currentAuctionRecipients) {
                currentAuctionRecipients.add(recipient);
            }
            System.out.println("Registering " + recipient.name);
            isSuccess = RecipientAuctionRegistrationStatus.SUCCESS;
        }
        finally {
            recipientLock.unlock();
            return isSuccess;
        }
    }

    //Starting new auction.
    private void StartAuction()  {

        //Start auction.
        isAuctionInProcess.set(true);

        //Wait 5 seconds
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        //Choose winner if there are any registered recipients.
        Item item;
        itemLock.lock();
        try
        {
            item = itemQueue.poll();
            Recipient winner = chooseWinner();
            if(winner == null)
            {
                System.out.println("There is no winner for " + item.name);
            }
            else {
                winner.AddOwnedItem(item);
                System.out.println("Winner for auction " + item.name + " is " + winner.name);
            }
            isAuctionInProcess.set(false);
        }
        finally {
            itemLock.unlock();
        }
    }

    //Choose winner of current auction.
    private Recipient chooseWinner()
    {
        Recipient winner;
        Random random = new Random();
        recipientLock.lock();
        try {
            if(currentAuctionRecipients.isEmpty())
            {
                winner = null;
            }
            else {
                //Choose random winner from currently registered recipients.
                winner = currentAuctionRecipients.get(random.nextInt(currentAuctionRecipients.size()));

                //Waking up all recipients who felt asleep after successful registration.
                Recipient[] recipientArray = {};
                recipientArray = currentAuctionRecipients.toArray(recipientArray);
                for (int i = 0; i < currentAuctionRecipients.size(); i++) {
                    synchronized (recipientArray[i]) {
                        recipientArray[i].notify();
                    }
                }

                //Clearing list of registered recipients.
                synchronized (currentAuctionRecipients) {
                    currentAuctionRecipients.clear();
                }
            }
        }
        finally {
            recipientLock.unlock();
        }
        return winner;
    }

    //Entrance point for chairman thread.
    @Override
    public void run() {

        //Waits for auction house to open.
        try {
            synchronized (this) {
                wait();
            }
        } catch (InterruptedException e) {}

        //Keep starting new auctions until there will be empty queue for more than 5 seconds.
        do {
            while (!itemQueue.isEmpty()) {
                StartAuction();
            }
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        } while(!itemQueue.isEmpty());

        //Notify market manager to close the market.
        System.out.println("No auctions within 5 seconds. Closing the market");
        synchronized (AuctionHouse.marketManager) {
            AuctionHouse.marketManager.notify();
        }

        //Wait for notification from market manager.
        try {
            synchronized (this) {
                wait();
            }
        } catch (InterruptedException e) {}
        System.out.println("Chairman says good bye");
    }
}
