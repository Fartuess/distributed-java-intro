package AdamBeczynski;

/**
 * Created by CLEVO on 2014-11-10.
 */
public class MarketManager implements Runnable {

    public volatile boolean isMarketOpened = false;

    public synchronized boolean RegisterDonor(Donor donor) {
        if(isMarketOpened)  return false; //market is opened nobody can register anymore
        if(!AuctionHouse.donors.containsKey(donor.name)) {
            AuctionHouse.donors.put(donor.name, donor);
            return true;
        }
        else
        {
            return false;
        }
    }

    public synchronized boolean RegisterRecipient(Recipient recipient)  {
        if(isMarketOpened)  return false; //market is opened nobody can register anymore
        if(!AuctionHouse.recipients.containsKey(recipient.name)) {
            AuctionHouse.recipients.put(recipient.name, recipient);
            return true;
        }
        else
        {
            return false;
        }
    }

    public void Start() {
        isMarketOpened = true;
        synchronized (AuctionHouse.chairman) {
            AuctionHouse.chairman.notify();
        }
        String[] donorsArray = {};
        donorsArray = AuctionHouse.donors.keySet().toArray(donorsArray);
        for (int i = 0; i < AuctionHouse.donors.size(); i++) {
            Donor it = AuctionHouse.donors.get(donorsArray[i]);
            //AuctionHouse.donors.remove(it.name);
            synchronized (it) {
                it.notify();
            }
        }
        String[] recipientsArray = {};
        recipientsArray = AuctionHouse.recipients.keySet().toArray(recipientsArray);
        for (int i = 0; i < AuctionHouse.recipients.size(); i++) {
            Recipient it = AuctionHouse.recipients.get(recipientsArray[i]);
            //AuctionHouse.recipients.remove(it.name);
            synchronized (it) {
                it.notify();
            }
        }
    }
    public void Close() {
        String[] donorsArray = {};
        donorsArray = AuctionHouse.donors.keySet().toArray(donorsArray);
        for (int i = 0; i < AuctionHouse.donors.size(); i++) {
            Donor it = AuctionHouse.donors.get(donorsArray[i]);
            AuctionHouse.donors.remove(it.name);
            synchronized (it) {
                it.notify();
            }
        }
        String[] recipientsArray = {};
        recipientsArray = AuctionHouse.recipients.keySet().toArray(recipientsArray);
        for (int i = 0; i < AuctionHouse.recipients.size(); i++) {
            Recipient it = AuctionHouse.recipients.get(recipientsArray[i]);
            AuctionHouse.recipients.remove(it.name);
            synchronized (it) {
                it.notify();
            }
        }
        synchronized (AuctionHouse.chairman) {
            AuctionHouse.chairman.notify();
        }
    }

    @Override
    public void run() {
        try {
            Thread.sleep(5000); //additional operation to give time for registration
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Start();
        try {
            synchronized (this) {
                wait();
            }
        } catch (InterruptedException e) {}
        Close();
    }
}
