package com.tacitknowledge.simulator;

import com.tacitknowledge.simulator.camel.RouteManager;
import com.tacitknowledge.simulator.formats.AdapterFactory;
import org.apache.log4j.Logger;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Jorge Galindo (jgalindo@tacitknowledge.com)
 */
public class ConversationManagerImpl implements ConversationManager
{
    /**
     * Logger for this class.
     */
    private static Logger logger = Logger.getLogger(ConversationManagerImpl.class);

    /**
     * Singleton instance
     */
    private static ConversationManager instance;

    /**
     * CamelRoutes manager
     */
    private RouteManager routeManager;

    /**
     * Currently configured conversations
     */
    private Map<Integer, Conversation> conversations = new HashMap<Integer, Conversation>();

    /**
     * Private Singleton constructor
     *
     * @param routeManager
     *            RouteManager to use
     */
    private ConversationManagerImpl(RouteManager routeManager)
    {
        this.routeManager = routeManager;
    }

    /**
     * Get the singleton instance of the ConversationManager
     *
     * @return ConversationManager singleton instance
     */
    public static synchronized ConversationManager getInstance()
    {
        if (instance == null)
        {
            try
            {
                instance = new ConversationManagerImpl(new RouteManager());
            }
            catch (Exception e)
            {
                logger.error("Critical error during Simulator initialization");

                throw new RuntimeException(e);
            }
        }
        return instance;
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
        Conversation conversation = ConversationFactory.createConversation(id, inboundTransport,
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
}
