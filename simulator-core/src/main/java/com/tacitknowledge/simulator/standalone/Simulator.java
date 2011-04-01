package com.tacitknowledge.simulator.standalone;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Entry point class for the application in standalone mode.
 *
 * @author Oleg Ciobanu ociobanu@taciteknowledge.com
 */
public class Simulator
{
    /** class logger. */
    private static Logger logger = LoggerFactory.getLogger(Simulator.class);

    private static ScheduledConversationsLoader scheduledConversationsLoader;

    /**
     * Application entry point in stand alone mode.
     *
     * @param args - not used for the moment
     */
    public static void main(String[] args)
    {
        scheduledConversationsLoader = new ScheduledConversationsLoader();

        scheduledConversationsLoader.start();

        addShutdownHook();
    }

    /** Adds shutdown hook that stops the ScheduledConversationsLoader in a graceful way */
    private static void addShutdownHook()
    {
        Runtime.getRuntime().addShutdownHook(new Thread(new Runnable()
        {
            public void run()
            {
                System.out.println("Stopping simulator...");

                scheduledConversationsLoader.setToStop(true);

                try
                {
                    scheduledConversationsLoader.interrupt();
                    scheduledConversationsLoader.join();
                }
                catch (InterruptedException e)
                {
                    logger.error(e.getMessage());
                }

            }
        }));
    }
}
