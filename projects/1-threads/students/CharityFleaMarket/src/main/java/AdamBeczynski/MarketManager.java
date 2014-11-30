package AdamBeczynski;

/**
 * Created by CLEVO on 2014-11-10.
 *
 * Market manager runnable.
 */
public class MarketManager implements Runnable {

    //current state of auction house.
    public enum AuctionHouseState
    {
        REGISTRATION,
        OPENED,
        CLOSED
    }
    public volatile AuctionHouseState auctionHouseState = AuctionHouseState.REGISTRATION;

    //Registration of donor.
    public synchronized boolean RegisterDonor(Donor donor) {
        synchronized (auctionHouseState) {
            if (auctionHouseState != AuctionHouseState.REGISTRATION) return false; //Cannot register. Market is not in registration state anymore.
        }
        if(!AuctionHouse.donors.containsKey(donor.name)) {
            AuctionHouse.donors.put(donor.name, donor);
            return true;    //Registration successful.
        }
        else
        {
            return false;   //Cannot register. Someone with this name is already registered.
        }
    }

    //Registration of Recipient.
    public synchronized boolean RegisterRecipient(Recipient recipient)  {
        synchronized (auctionHouseState) {
            if (auctionHouseState != AuctionHouseState.REGISTRATION)    return false; //Cannot register. Market is not in registration state anymore.
        }
        if(!AuctionHouse.recipients.containsKey(recipient.name)) {
            AuctionHouse.recipients.put(recipient.name, recipient);
            return true;    //Registration successful.
        }
        else
        {
            return false;   //Cannot register. Someone with this name is already registered.
        }
    }

    //Opening the market and starting auctions.
    private void Start() {

        //Changing auction house state. Auction house is now opened.
        synchronized (auctionHouseState)
        {
            auctionHouseState = AuctionHouseState.OPENED;
        }

        //Awaking chairman.
        synchronized (AuctionHouse.chairman) {
            AuctionHouse.chairman.notify();
        }

        //Awaking donors. They will start donating items now.
        String[] donorsArray = {};
        donorsArray = AuctionHouse.donors.keySet().toArray(donorsArray);
        for (int i = 0; i < AuctionHouse.donors.size(); i++) {
            Donor it = AuctionHouse.donors.get(donorsArray[i]);
            synchronized (it) {
                it.notify();
            }
        }

        //Awaking recipients. They will start registering for item auctions.
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

    //Closing the market and asking everybody to leave.
    private void Close() {

        //Changing auction house state. Auction house is now closed.
        synchronized (auctionHouseState)
        {
            auctionHouseState = AuctionHouseState.CLOSED;
        }

        //Awaking donors. They will leave market now.
        String[] donorsArray = {};
        donorsArray = AuctionHouse.donors.keySet().toArray(donorsArray);
        int donorSize = AuctionHouse.donors.size();
        for (int i = 0; i < donorSize; i++) {
            Donor it = AuctionHouse.donors.get(donorsArray[i]);
            AuctionHouse.donors.remove(it.name);
            synchronized (it) {
                it.notify();
            }
        }

        //Awaking recipients. They will leave market now. *Actually not needed as recipients leave market when
        // they figure out that auction house state is CLOSED.
        String[] recipientsArray = {};
        recipientsArray = AuctionHouse.recipients.keySet().toArray(recipientsArray);
        int recipientSize = AuctionHouse.recipients.size();
        for (int i = 0; i < recipientSize; i++) {
            Recipient it = AuctionHouse.recipients.get(recipientsArray[i]);
            AuctionHouse.recipients.remove(it.name);
            synchronized (it) {
                it.notify();
            }
        }

        //Awaking chairman. He will leave market now.
        synchronized (AuctionHouse.chairman) {
            AuctionHouse.chairman.notify();
        }
    }

    //Entrance point for Market manager thread.
    @Override
    public void run() {

        //Additional operation to give time for registration
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        //Opening market and starting auctions.
        Start();

        //Suspending market manager thread until notified by chairman to close the market.
        try {
            synchronized (this) {
                wait();
            }
        } catch (InterruptedException e) {}

        //Closing the market and asking everybody to leave.
        Close();
    }
}
