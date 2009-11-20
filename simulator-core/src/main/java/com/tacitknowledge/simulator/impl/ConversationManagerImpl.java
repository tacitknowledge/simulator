package com.tacitknowledge.simulator.impl;

import com.tacitknowledge.simulator.Adapter;
import com.tacitknowledge.simulator.Conversation;
import com.tacitknowledge.simulator.ConversationManager;
import com.tacitknowledge.simulator.ConversationNotFoundException;
import com.tacitknowledge.simulator.RouteManager;
import com.tacitknowledge.simulator.SimulatorException;
import com.tacitknowledge.simulator.Transport;

import com.tacitknowledge.simulator.camel.RouteManagerImpl;
import com.tacitknowledge.simulator.formats.AdapterFactory;

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

    /**
     * Public constructor for ConversationManagerImpl.
     *
     * @param routeManager
     *            RouteManager to use
     */
    public ConversationManagerImpl(RouteManager routeManager)
    {
        this.routeManager = routeManager;
    }

    /**
     * Constructor.
     * With this constructor, the manager will create its own RouteManager
     */
    public ConversationManagerImpl()
    {
        this.routeManager = new RouteManagerImpl();
    }
    /**
     * {@inheritDoc}
     */
    public Conversation createConversation(Integer id, Transport inboundTransport,
            Transport outboundTransport, String inboundFormat, String outboundFormat)
            throws SimulatorException
    {
        Adapter inAdapter = AdapterFactory.getAdapter(inboundFormat);
        Adapter outAdapter = AdapterFactory.getAdapter(inboundFormat);
        ConversationImpl conversation = ConversationFactory.createConversation(id, inboundTransport,
                outboundTransport, inAdapter, outAdapter);
        assert conversations.get(id) == null;

        conversations.put(id, conversation);

        logger.debug("Created new conversation with id : " + id);

        return conversation;
    }

    /**
     * @param conversationId
     *            conversationId
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
     * @inheritDoc
     * @param conversationId the id of the conversation to be created
     * @param language
     *            The scripting language for the scenario. This would be System wide.
     * @param criteria
     *            The criteria script
     * @param transformation
     *            The transformation script
     */
    public void createConversationScenario(int conversationId, String language, String criteria,
            String transformation)
    {
        Conversation conversation = conversations.get(conversationId);

        if (conversation != null)
        {
            conversation.addScenario(language, criteria, transformation);
        }
    }

    /**
     * @inheritDoc
     * @param conversationId conversation id of the conversation to be activated.
     * @throws ConversationNotFoundException exception.
     * @throws SimulatorException exception.
     */
    public void activate(int conversationId) throws ConversationNotFoundException,
            SimulatorException
    {
        Conversation conversation = getConversationById(conversationId);
        try
        {
            routeManager.activate(conversation);
        }
        catch (Exception e)
        {
            logger.error("Conversation with id : "
                    + conversationId + " couldn't be activated.", e);

            throw new SimulatorException("Conversation activation exception", e);
        }
    }

    /**
     * @inheritDoc
     * @param conversationId conversation id of the conversation to be deactivated.
     * @throws ConversationNotFoundException exception.
     * @throws SimulatorException exception.
     */
    public void deactivate(int conversationId) throws ConversationNotFoundException,
            SimulatorException
    {

        Conversation conversation = getConversationById(conversationId);
        try
        {
            routeManager.deactivate(conversation);
        }
        catch (Exception e)
        {
            logger.error("Conversation with id : "
                    + conversationId + " couldn't be deactivated.", e);

            throw new SimulatorException("Conversation deactivation exception", e);
        }

    }

    /**
     * @inheritDoc
     * @param conversationId conversation id of the conversation to be deleted.
     * @throws SimulatorException exception.
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
     * @inheritDoc
     * @param format @see ConversationManager#getAdapterParameters
     * @return @see ConversationManager#getAdapterParameters
     */
    public List<List> getAdapterParameters(String format)
    {
        return AdapterFactory.getAdapterParameters(format);
    }

    /**
     * @inheritDoc
     * @param type The transport type
     * @return @see ConversationManager#getTransportParameters
     */
    public List<List> getTransportParameters(String type)
    {
        return TransportFactory.getTransportParameters(type);
    }
}
