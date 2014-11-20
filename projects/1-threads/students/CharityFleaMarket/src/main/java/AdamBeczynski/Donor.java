package AdamBeczynski;

import java.util.Random;

/**
 * Created by CLEVO on 2014-11-10.
 */
public class Donor implements Runnable
{
    public String name;
    public Donor(String name)
    {
        this.name = name;
    }
    private void RegisterItem(){
        Chairman.ItemRegistrationStatus helper = Chairman.ItemRegistrationStatus.OTHER_FAILURE;
        Item myItem = new Item("Item_" + name);
        Random random = new Random();
        //System.out.println("Adding Item!");
        try {
            Thread.sleep(random.nextInt(8000)/* + 1000*/);
        }
        catch (InterruptedException e)  {}
        do {
            helper = AuctionHouse.chairman.addItem(myItem);
            if (helper == Chairman.ItemRegistrationStatus.QUEUE_FULL)
            {
                try {
                    Thread.sleep(5000);
                }
                catch (InterruptedException e)  {}
            }
        } while (helper == Chairman.ItemRegistrationStatus.QUEUE_FULL);
        //System.out.println("I've done my job!");
    }

    @Override
    public void run() {
        //System.out.println("Donor Reporting!");
        AuctionHouse.marketManager.RegisterDonor(this);
        try {
            synchronized (this) {
                wait();
            }
        } catch (InterruptedException e) {}
        RegisterItem();
        try {
            synchronized (this) {
                wait();
            }
        } catch (InterruptedException e) {}
        System.out.println(name + " says good bye");
    }
}
