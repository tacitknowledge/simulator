package com.tacitknowledge.simulator.impl;

import com.tacitknowledge.simulator.*;
import com.tacitknowledge.simulator.camel.RouteManagerImpl;
import com.tacitknowledge.simulator.formats.AdapterFactory;
import com.tacitknowledge.simulator.scripting.ScriptExecutionService;
import com.tacitknowledge.simulator.transports.TransportFactory;
import org.apache.log4j.Logger;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    private static Logger logger = Logger.getLogger(ConversationManagerImpl.class);

    /**
     * CamelRoutes manager
     */
    private RouteManager routeManager;

    /**
     * Currently configured conversations
     */
    private Map<Integer, ConversationImpl> conversations = new HashMap<Integer, ConversationImpl>();


    private static final ConversationManager instance = new ConversationManagerImpl();


    public static ConversationManager getInstance() {
        return instance;
    }

    /**
     * Public constructor for ConversationManagerImpl.
     *
     * @param routeManager RouteManager to use
     */
    ConversationManagerImpl(RouteManager routeManager)
    {
        this.routeManager = routeManager;
    }

    /**
     * Constructor.
     * With this constructor, the manager will create its own RouteManager
     */
    ConversationManagerImpl()
    {
        this.routeManager = new RouteManagerImpl();
    }

    /**
     * {@inheritDoc}
     */
    public Conversation createConversation(Integer id, Transport inboundTransport,
        Transport outboundTransport, Adapter inAdapter, Adapter outAdapter)
        throws SimulatorException
    {
        ConversationImpl conversation = ConversationFactory.createConversation(id, inboundTransport,
                outboundTransport, inAdapter, outAdapter);
        assert conversations.get(id) == null;

        conversations.put(id, conversation);

        logger.info("Created new conversation with id : " + id);

        return conversation;
    }

    /**
     * @param conversationId conversationId
     * @return conversation from the list of created conversations
     * @throws ConversationNotFoundException in case conversation is not found
     */
    private Conversation getConversationById(int conversationId)
            throws ConversationNotFoundException
    {
        Conversation conversation = conversations.get(conversationId);
        if (conversation == null)
        {
            logger.error("Conversation with id : " + conversationId + " is not found.");

            throw new ConversationNotFoundException("Conversation with id " + conversationId
                    + " is not created.");
        }
        return conversation;
    }

    /**
     * @param conversationId the id of the conversation to be created
     * @param scenarioId
     * @param language       The scripting language for the scenario. This would be System wide.
     * @param criteria       The criteria script
     * @param transformation The transformation script    @inheritDoc
     */
    public ConversationScenario createOrUpdateConversationScenario(int conversationId, int scenarioId, String language, String criteria,
                                           String transformation)
    {
        Conversation conversation = conversations.get(conversationId);
        ConversationScenario conversationScenario=null;

        if (conversation != null)
        {
            conversationScenario = conversation.addOrUpdateScenario(scenarioId, language, criteria, transformation);
        }
        return conversationScenario;
    }

    /**
     * @param conversationId conversation id of the conversation to be activated.
     * @throws ConversationNotFoundException exception.
     * @throws SimulatorException            exception.
     * @inheritDoc
     */
    public void activate(int conversationId) throws ConversationNotFoundException,
            SimulatorException
    {
        Conversation conversation = getConversationById(conversationId);
        try
        {
            routeManager.activate(conversation);
            logger.debug("Activated conversation " + conversation);
        }
        catch (Exception e)
        {
            logger.error("Conversation with id : "
                    + conversationId + " couldn't be activated.", e);

            throw new SimulatorException("Conversation activation exception", e);
        }
    }

    /**
     * @param conversationId conversation id of the conversation to be deactivated.
     * @throws ConversationNotFoundException exception.
     * @throws SimulatorException            exception.
     * @inheritDoc
     */
    public void deactivate(int conversationId) throws ConversationNotFoundException,
            SimulatorException
    {

        Conversation conversation = getConversationById(conversationId);
        try
        {
            routeManager.deactivate(conversation);
            conversations.remove(conversationId);
            logger.debug("Deactivated conversation " + conversation);

        }
        catch (Exception e)
        {
            logger.error("Conversation with id : "
                    + conversationId + " couldn't be deactivated.", e);

            throw new SimulatorException("Conversation deactivation exception", e);
        }

    }

    /**
     * @param conversationId conversation id of the conversation to be deleted.
     * @throws SimulatorException exception.
     * @inheritDoc
     */
    public void deleteConversation(int conversationId) throws SimulatorException
    {
        try
        {
            deactivate(conversationId);
            conversations.remove(conversationId);
        }
        catch (ConversationNotFoundException e)
        {
            logger.error("Conversation with id : " + conversationId + " was not found.", e);
        }
    }

    /**
     * @param format @see ConversationManager#getAdapterParameters
     * @return @see ConversationManager#getAdapterParameters
     * @inheritDoc
     */
    public List<List> getAdapterParameters(String format)
    {
        return AdapterFactory.getAdapterParameters(format);
    }

    /**
     * @param type The transport type
     * @return @see ConversationManager#getTransportParameters
     * @inheritDoc
     */
    public List<List> getTransportParameters(String type)
    {
        return TransportFactory.getTransportParameters(type);
    }

    /**
     * @param name class name
     * @return instance of the class
     * @inheritDoc
     */
    public Object getClassByName(String name)
    {
        try
        {
            return Class.forName(name).newInstance();
        }
        catch (InstantiationException e)
        {
            e.printStackTrace();
        }
        catch (IllegalAccessException e)
        {
            e.printStackTrace();
        }
        catch (ClassNotFoundException e)
        {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * {@inheritDoc}
     */
    public boolean isActive(int conversationId) throws SimulatorException
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

    public String[][] getAvailableLanguages() {
        return ScriptExecutionService.getAvailableLanguages();
    }
}
