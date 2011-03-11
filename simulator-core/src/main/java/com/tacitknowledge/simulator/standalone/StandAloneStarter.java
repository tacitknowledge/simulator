package com.tacitknowledge.simulator.standalone;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tacitknowledge.simulator.utils.Configuration;

/**
 * Entry point class for the application in standalone mode.
 * @author Oleg Ciobanu ociobanu@taciteknowledge.com
 *
 */
public class StandAloneStarter
{
    /**
     * class logger.
     */
    private static Logger logger = LoggerFactory.getLogger(StandAloneStarter.class);

    /**
     * Application entry point in stand alone mode.
     * @param args - not used for the moment
     */
    public static void main(String[] args)
    {
        try
        {

            if (args != null && args.length > 2)
            {
                Configuration.path = args[1];
            }

            // Initiate conversations scheduled
            // loader and close command waiter.
            ScheduledConversationsLoader loader = new ScheduledConversationsLoader();
            CloseCommandWaiter closeWaiter = new CloseCommandWaiter();
            closeWaiter.setConversationsScheduledLoader(loader);

            // start "file loader" thread and "close command waiter" thread
            loader.start();
            closeWaiter.start();
        }
        catch (IOException e)
        {
            logger.error(e.getMessage());
        }
    }
}
