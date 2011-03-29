package com.tacitknowledge.simulator.standalone;

import java.io.File;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tacitknowledge.simulator.ConversationManager;
import com.tacitknowledge.simulator.RouteManager;
import com.tacitknowledge.simulator.camel.RouteManagerImpl;
import com.tacitknowledge.simulator.configuration.loaders.ConversationLoader;
import com.tacitknowledge.simulator.configuration.loaders.ScenarioLoader;
import com.tacitknowledge.simulator.impl.ConversationManagerImpl;
import com.tacitknowledge.simulator.utils.Configuration;

/**
 * This class contains the logic of loading the conversations
 *  with a certain frequency.
 * @author Oleg Ciobanu ociobanu@tacitknowledge.com
 *
 */
public class ScheduledConversationsLoader extends Thread
{
    /**
     *  stop flag.
     */
    private Boolean toStop = Boolean.FALSE;

    /**
     * Route manager for camel context. 
     */
    private RouteManager routeManager;

    /**
     * class logger.
     */
    private static Logger logger = LoggerFactory.getLogger(ScheduledConversationsLoader.class);

    private ScenarioLoader scenarioLoader;

    private ConversationLoader conversationLoader;

    private ConversationManager conversationManager;

    /**
     * The constructor will load from config files all interested configs.
     */
    public ScheduledConversationsLoader()
    {
        routeManager = new RouteManagerImpl();
        scenarioLoader = new ScenarioLoader();
        conversationLoader = new ConversationLoader(scenarioLoader);

        conversationManager = new ConversationManagerImpl(routeManager, conversationLoader);

        startRouteManager();
    }

    /**
    * toStop getter.
    * @return toStop flag
    */
    public synchronized Boolean getToStop()
    {
        return toStop;
    }

    /**
     * toStop setter.
     * @param toStop
     */
    public synchronized void setToStop(boolean toStop)
    {
        this.toStop = toStop;
    }

    /**
     * conversations scheduled loader will load configurations
     * from the the files with a configured frequency.
     */
    @Override
    public void run()
    {
        while (!getToStop())
        {
            try
            {
                loadConversations();
            }
            catch (Exception e1)
            {
                logger.error(e1.getMessage());
            }

            //wait a certain amount of time
            synchronized (this)
            {
                try
                {
                    Thread.currentThread()
                            .wait(Configuration
                                    .getPropertyAsInt(Configuration.CONVERSATIONS_READING_FREQUENCY_NAME));
                }
                catch (InterruptedException e)
                {
                    logger.warn(e.getMessage());
                }
            }
        }

        stopRouteManager();
    }

    protected String getSystemsDirectory()
    {
        return Configuration.getPropertyAsString(Configuration.SYSTEMS_DIRECTORY_NAME);
    }

    /**
     * Load conversations from the file system.
     * @throws Exception
     */
    public void loadConversations() throws Exception
    {
        String systemsDirectory = getSystemsDirectory();
        
        File resource = new File(systemsDirectory);
        
        if (!resource.exists())
        {
            logger.error("Could not find systems directory '" + systemsDirectory
                    + "'. Stopping application");
            System.exit(1);
        }

        conversationManager.loadConversations(systemsDirectory);
    }

    private void startRouteManager()
    {
        try
        {
            routeManager.start();
        }
        catch (Exception e)
        {
            logger.error("Route manager could not be started.", e);
            throw new RuntimeException("Route manager could not be started.");
        }
    }

    private void stopRouteManager()
    {
        try
        {
            routeManager.stop();
        }
        catch (Exception e)
        {
            logger.error("Route manager could not be stoped.", e);
            throw new RuntimeException("Route manager could not be stoped.");
        }
    }
}
