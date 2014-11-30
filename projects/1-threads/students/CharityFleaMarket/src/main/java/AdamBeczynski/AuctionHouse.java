package AdamBeczynski;

import java.util.HashMap;

/**
 * Created by CLEVO on 2014-11-10.
 *
 * Class created to make project environment simpler and not using singletons. Potentially this class could be turn
 * into singleton and keep non-static non-singleton variables for market manager and chairman.
 */
public class AuctionHouse {
    public static volatile MarketManager marketManager = new MarketManager();
    public static volatile Chairman chairman = new Chairman();
    public static volatile HashMap<String, Donor> donors = new HashMap<String, Donor>();
    public static volatile HashMap<String, Recipient> recipients = new HashMap<String, Recipient>();
}
