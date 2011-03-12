package com.tacitknowledge.simulator.standalone;

import java.io.File;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tacitknowledge.simulator.Conversation;
import com.tacitknowledge.simulator.RouteManager;
import com.tacitknowledge.simulator.camel.RouteManagerImpl;
import com.tacitknowledge.simulator.configuration.loaders.ConversationLoader;
import com.tacitknowledge.simulator.configuration.loaders.ConversationsLoader;
import com.tacitknowledge.simulator.configuration.loaders.ScenarioLoader;
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

    private ConversationsLoader conversationsLoader;

    /**
     * The constructor will load from config files all interested configs.
     */
    public ScheduledConversationsLoader()
    {
        routeManager = new RouteManagerImpl();
        scenarioLoader = new ScenarioLoader();
        conversationLoader = new ConversationLoader(scenarioLoader);
        conversationsLoader = new ConversationsLoader(conversationLoader);
        
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
            // TODO
            // reload conversations from files every
            // conversationReadingFrequency milliseconds
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
    
    /**
     * Load conversations from the file system.
     * @throws Exception
     */
    public void loadConversations() throws Exception
    {
        File resource = new File(Configuration.getPropertyAsString(Configuration.SYSTEMS_DIRECTORY_NAME));

        List<Conversation> conversations = conversationsLoader.loadConversations(resource
                .getAbsolutePath());
        
        for (Conversation conversation : conversations)
        {
            routeManager.activate(conversation);
        }
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
