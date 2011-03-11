package com.tacitknowledge.simulator.impl;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tacitknowledge.simulator.Adapter;
import com.tacitknowledge.simulator.Conversation;
import com.tacitknowledge.simulator.ConversationManager;
import com.tacitknowledge.simulator.ConversationNotFoundException;
import com.tacitknowledge.simulator.ConversationScenario;
import com.tacitknowledge.simulator.RouteManager;
import com.tacitknowledge.simulator.SimulatorException;
import com.tacitknowledge.simulator.Transport;
import com.tacitknowledge.simulator.camel.RouteManagerImpl;
import com.tacitknowledge.simulator.configuration.EventDispatcher;
import com.tacitknowledge.simulator.configuration.SimulatorEventListener;
import com.tacitknowledge.simulator.scripting.ScriptExecutionService;

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

    /** The singleton version of the class */
    private static ConversationManagerImpl instance = new ConversationManagerImpl();

    /**
     * CamelRoutes manager
     */
    private RouteManager routeManager;

    /**
     * Currently configured conversations
     */
    private Map<String, Conversation> conversations;

    /**
     * The method can be used to get a pre-created version of the ConversationManager as a singleton.
     * Note that the class can still be created using the constructor so that it's not quite the
     * implementation of the singleton pattern.
     */
    public static ConversationManagerImpl getInstance()
    {
        return instance;
    }

    /**
     * Public constructor for ConversationManagerImpl.
     *
     * @param routeManager RouteManager to use
     */
    public ConversationManagerImpl(final RouteManager routeManager)
    {
        this.routeManager = routeManager;
        this.conversations = new HashMap<String, Conversation>();
    }

    /**
     * Constructor.
     * With this constructor, the manager will create its own RouteManager
     */
    public ConversationManagerImpl()
    {
        this(new RouteManagerImpl());
    }

    /**
     * {@inheritDoc}
     */
    public Conversation createOrUpdateConversation(final String id,
            final Transport inboundTransport, final Transport outboundTransport,
            final Adapter inAdapter, final Adapter outAdapter)
    {
        Conversation conversationObj = conversations.get(id);

        if (conversationObj != null)
        {
            logger.info("Removing existing conversation from cache: Id = {}", id);
            conversations.remove(conversationObj);
        }

        ConversationImpl conversation = ConversationFactory.createConversation(id,
                inboundTransport, outboundTransport, inAdapter, outAdapter);
        assert conversations.get(id) == null;

        conversations.put(id, conversation);

        logger.info("Created new conversation with id : {}", id);

        return conversation;
    }

    /**
     * @param conversationId conversationId
     * @return conversation from the list of created conversations
     * @throws ConversationNotFoundException in case conversation is not found
     */
    private Conversation getConversationById(final String conversationId)
            throws ConversationNotFoundException
    {
        Conversation conversation = conversations.get(conversationId);
        if (conversation == null)
        {
            throw new ConversationNotFoundException("Conversation with id " + conversationId
                    + " is not created.");
        }
        return conversation;
    }

    /**
     * {@inheritDoc}
     */
    public ConversationScenario createOrUpdateConversationScenario(final String conversationId,
            final int scenarioId, final String language, final String criteria,
            final String transformation)
    {
        Conversation conversation = conversations.get(conversationId);
        ConversationScenario conversationScenario = null;

        if (conversation != null)
        {
            conversationScenario = conversation.addScenario(language, criteria, transformation);
        }

        return conversationScenario;
    }

    /**
     * {@inheritDoc}
     */
    public void activate(final String conversationId) throws ConversationNotFoundException,
            SimulatorException
    {
        Conversation conversation = getConversationById(conversationId);
        try
        {
            routeManager.activate(conversation);
            logger.debug("Activated conversation {}", conversation);
        }
        catch (Exception e)
        {
            String errorMsg = "Conversation with id : " + conversationId
                    + " couldn't be activated: ";
            throw new SimulatorException(errorMsg, e);
        }
    }

    /**
     * {@inheritDoc}
     */
    public void deactivate(final String conversationId) throws SimulatorException
    {
        try
        {
            Conversation conversation = getConversationById(conversationId);
            routeManager.deactivate(conversation);
            logger.debug("Deactivated conversation {}", conversation);
        }
        catch (Exception e)
        {
            String errorMessage = "Conversation deactivation exception. Conversation with id :"
                    + conversationId + " couldn't be deactivated.";
            throw new SimulatorException(errorMessage, e);
        }
    }

    /**
     * {@inheritDoc}
     */
    public void deleteConversation(final String conversationId) throws SimulatorException
    {
        try
        {
            Conversation conversation = conversations.get(conversationId);
            if (conversation != null)
            {
                routeManager.delete(conversation);
                conversations.remove(conversationId);
            }
        }
        catch (Exception e)
        {
            String errorMsg = "Conversation with id " + conversationId + " couldn't be deleted: ";
            throw new SimulatorException(errorMsg, e);
        }
    }

    /**
     * {@inheritDoc}
     */
    public boolean conversationExists(final String conversationId)
    {
        return conversations.containsKey(conversationId);
    }

    /**
     * @param name class name
     * @return instance of the class
     * @throws ClassNotFoundException If a class with the passed name was not found
     * @throws IllegalAccessException If the class was access from the incorrect context
     * @throws InstantiationException If the class couldn't be instantiated
     * @inheritDoc
     */
    public Object getClassByName(final String name) throws ClassNotFoundException,
            IllegalAccessException, InstantiationException
    {
        return Class.forName(name).newInstance();
    }

    /**
     * {@inheritDoc}
     */
    public boolean isActive(final String conversationId) throws SimulatorException
    {
        try
        {
            Conversation conversation = getConversationById(conversationId);
            return routeManager.isActive(conversation);
        }
        catch (ConversationNotFoundException e)
        {
            return false;
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
