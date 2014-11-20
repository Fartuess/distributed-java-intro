package AdamBeczynski;

import com.sun.jmx.remote.internal.ArrayQueue;

import java.util.ArrayList;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Random;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by CLEVO on 2014-11-10.
 */


public class Chairman implements Runnable {
    //public PriorityQueue<Item> itemQueue = new PriorityQueue<Item>();
    public volatile ArrayBlockingQueue<Item> itemQueue = new ArrayBlockingQueue<Item>(10);
    private volatile ArrayList<Recipient> currentAuctionRecipients = new ArrayList<Recipient>();

    //private final ReentrantLock auctionInProcessLock = new ReentrantLock();
    private volatile boolean isAuctionInProcess = false;
    private final ReentrantLock itemLock = new ReentrantLock();
    private final ReentrantLock recipentLock = new ReentrantLock();

    public enum ItemRegistrationStatus
    {
        NO_AUCTION_IN_PROGRESS,
        QUEUE_FULL,
        SUCCESS,
        OTHER_FAILURE
    }

    public enum RecipientAuctionRegistrationStatus
    {
        NO_AUCTION_IN_PROGRESS,
        SUCCESS,
        OTHER_FAILURE
    }

    public synchronized ItemRegistrationStatus addItem(Item item)  //is synchronized needed?
    {
        if(!AuctionHouse.marketManager.isMarketOpened)
        {
            //System.out.println("Auctions haven't started, stop throwing items at me plox!");
            return ItemRegistrationStatus.NO_AUCTION_IN_PROGRESS;
        }
        ItemRegistrationStatus isSuccess = ItemRegistrationStatus.OTHER_FAILURE;
        //if(itemLock.isLocked()) System.out.println("Ima locked Man");
        itemLock.lock();
        try {
            if (itemQueue.size() >= 10)
            {
                //System.out.println("Queueueueue full!");
                isSuccess = ItemRegistrationStatus.QUEUE_FULL;
            }
            else
            {
                itemQueue.add(item);
                isSuccess = ItemRegistrationStatus.SUCCESS;
                //System.out.println("Item Successfully Added!");
            }
        }
        finally {
            itemLock.unlock();
            return isSuccess;
        }
    }
    public synchronized RecipientAuctionRegistrationStatus registerRecipient(Recipient recipient) //is synchronized needed?
    {
        if(!isAuctionInProcess) return RecipientAuctionRegistrationStatus.NO_AUCTION_IN_PROGRESS; //no auctions already in process? Sorry buddy!

        RecipientAuctionRegistrationStatus isSuccess = RecipientAuctionRegistrationStatus.OTHER_FAILURE;
        recipentLock.lock();
        try {
            currentAuctionRecipients.add(recipient);
            recipient.isRegistedToCurrentAuction = true;
            System.out.println("Registering " + recipient.name);
            isSuccess = RecipientAuctionRegistrationStatus.SUCCESS;
        }
        finally {
            recipentLock.unlock();
            return isSuccess;
        }
    }

    public void StartAuction()  {
        recipentLock.lock();
        try {
            currentAuctionRecipients.clear();
            isAuctionInProcess = true;
        }
        finally {
            recipentLock.unlock();
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
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
                isAuctionInProcess = false;
            }
            finally {
                itemLock.unlock();
            }
        }
    }

    private Recipient chooseWinner()
    {
        Recipient winner;
        Random random = new Random();
        recipentLock.lock();
        try {
            if(currentAuctionRecipients.isEmpty())
            {
                winner = null;
            }
            else {
                winner = currentAuctionRecipients.get(random.nextInt(currentAuctionRecipients.size()));
                Recipient[] recipientArray = {};
                recipientArray = currentAuctionRecipients.toArray(recipientArray);
                for (int i = 0; i < currentAuctionRecipients.size(); i++) {
                    synchronized (recipientArray[i]) {
                        recipientArray[i].notify();
                    }
                }
                currentAuctionRecipients.clear();
            }
        }
        finally {
            recipentLock.unlock();
        }
        return winner;
    }

    @Override
    public void run() {
        //System.out.println("Chairman have Arrived!");
        try {
            synchronized (this) {
                wait();
            }
        } catch (InterruptedException e) {}
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
        System.out.println("No auctions within 5 seconds. Closing the market");
        synchronized (AuctionHouse.marketManager) {
            AuctionHouse.marketManager.notify();
        }
        try {
            synchronized (this) {
                wait();
            }
        } catch (InterruptedException e) {}
        System.out.println("Chairman says good bye");
    }
}
