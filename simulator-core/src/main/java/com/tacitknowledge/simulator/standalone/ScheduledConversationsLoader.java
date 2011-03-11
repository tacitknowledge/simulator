package com.tacitknowledge.simulator.standalone;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.ResourceBundle;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import com.tacitknowledge.simulator.BaseConfigurable;
import com.tacitknowledge.simulator.Conversation;
import com.tacitknowledge.simulator.RouteManager;
import com.tacitknowledge.simulator.camel.RouteManagerImpl;
import com.tacitknowledge.simulator.configuration.loaders.ConversationsLoader;
import com.tacitknowledge.simulator.utils.Configuration;

/**
 * This class contains the logic of loading the conversations
 *  with a certain frequency.
 * @author Oleg Ciobanu ociobanu@tacitknowledge.com
 *
 */
public class ScheduledConversationsLoader extends Thread {

    /**
     *  stop flag.
     */
    private Boolean toStop = Boolean.FALSE;
    /**
     * Route manager for camel context. 
     */
    RouteManager routeManager;
    /**
     * class logger.
     */
    private static Logger logger = LoggerFactory.getLogger(ScheduledConversationsLoader.class);
    /**
     * The constructor will load from config files all interested configs.
     */
    public ScheduledConversationsLoader()
    {
        routeManager = new RouteManagerImpl();
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
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }
    }
    
    public void loadConversations() throws Exception{
        
        File resource = new File(
                Configuration.getPropertyAsString(Configuration.SYSTEMS_DIRECTORY));
        
        ConversationsLoader conversationLoader = new ConversationsLoader();
        
        List<Conversation> conversations = conversationLoader.loadConversations(resource.getAbsolutePath());
        for (Conversation conversation : conversations)
        {
            // TODO: refactor this
            // Workaround: relative paths don't work from junit and camel :) Don't know why yet.
            // workaroundAbsolutePaths((BaseConfigurable) conversation.getInboundTransport());
            // workaroundAbsolutePaths((BaseConfigurable) conversation.getOutboundTransport());

            routeManager.activate(conversation);
        }
    }
}
