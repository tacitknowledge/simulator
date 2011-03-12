package com.tacitknowledge.simulator.impl;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Map;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tacitknowledge.simulator.Conversation;
import com.tacitknowledge.simulator.ConversationManager;
import com.tacitknowledge.simulator.RouteManager;
import com.tacitknowledge.simulator.configuration.EventDispatcher;
import com.tacitknowledge.simulator.configuration.SimulatorEventListener;
import com.tacitknowledge.simulator.configuration.loaders.ConversationsLoader;

/**
 * Conversation manager implementation.
 *
 * @author Jorge Galindo (jgalindo@tacitknowledge.com)
 */
public class ConversationManagerImpl implements ConversationManager
{
    /**
     * Logger for this class.
     */
    private static Logger logger = LoggerFactory.getLogger(ConversationManagerImpl.class);

    private RouteManager routeManager;

    private ConversationsLoader conversationsLoader;

    public ConversationManagerImpl(RouteManager routeManager,
            ConversationsLoader conversationsLoader)
    {
        this.routeManager = routeManager;
        this.conversationsLoader = conversationsLoader;
    }

    /**
     * {@inheritDoc}
     */
    public void loadConversations(String systemsDirectoryPath) throws Exception
    {
        Map<String, Conversation> conversations = conversationsLoader.loadConversations(systemsDirectoryPath);

        for (Entry<String, Conversation> entry : conversations.entrySet())
        {
            Conversation conversation = entry.getValue();
            routeManager.activate(conversation);
        }
    }

    /**
     * {@inheritDoc}
     */
    public void registerListeners(final String filePath)
    {
        if (filePath != null)
        {
            File file = new File(filePath);
            if (file.exists() && file.canRead())
            {
                BufferedReader reader = null;
                try
                {
                    reader = new BufferedReader(new FileReader(file));
                    String line;
                    while ((line = reader.readLine()) != null)
                    {
                        line = line.trim();
                        if (line.length() > 0 && !line.startsWith("#"))
                        {
                            registerListenerImplementation(line);
                        }
                    }
                }
                catch (IOException ex)
                {
                    logger.info("Exception while trying to read listener file.", ex);
                }
                finally
                {
                    if (null != reader)
                    {
                        try
                        {
                            reader.close();
                        }
                        catch (IOException e)
                        {
                            logger.info("Unexpected IO exception: ", e);
                        }
                    }
                }
            }
            else
            {
                logger.info("Listeners not registered. Listener file not accesible.");
            }
        }
        else
        {
            logger.info("Listeners not registered. Listener file location is null.");
        }
    }

    /**
     * Register a SimulatorEventListener implementation in the EventDispatcher
     *
     * @param className - Qualified Class Name
     */
    @SuppressWarnings("rawtypes")
    private void registerListenerImplementation(final String className)
    {
        Class listenerClass;
        try
        {
            listenerClass = Class.forName(className);
            Object instanceObject = listenerClass.newInstance();
            EventDispatcher.getInstance().addSimulatorEventListener(
                    (SimulatorEventListener) instanceObject);
            logger.info("Registered class: {}", listenerClass);
        }
        catch (Exception e)
        {
            logger.info("Unable to register listener class: " + className + " due to:", e);
        }
    }
}
