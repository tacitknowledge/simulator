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
    private static ConversationManager _instance;

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
     * @param routeManager RouteManager to use
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
        if (_instance == null)
        {
            try
            {
                _instance = new ConversationManagerImpl(new RouteManager());
            }
            catch (Exception e)
            {
                logger.error("Critical error during Simulator initialization");
                throw new RuntimeException(e);
            }
        }
        return _instance;
    }

    /**
     * @inheritDoc
     */
    public Conversation createConversation(Integer id, Transport inboundTransport, Transport outboundTransport, String inboundFormat, String outboundFormat) throws UnsupportedFormatException
    {
        Adapter inAdapter = AdapterFactory.getAdapter(inboundFormat);
        Adapter outAdapter = AdapterFactory.getAdapter(inboundFormat);
        Conversation conversation =
                ConversationFactory.createConversation(id, inboundTransport, outboundTransport, inAdapter, outAdapter);
        assert conversations.get(id) == null;
        conversations.put(id, conversation);
        return conversation;
    }

    /**
     * @param id conversationId
     * @return conversation from the list of created conversations
     * @throws ConversationNotFoundException
     */
    private Conversation getConversationById(int id) throws ConversationNotFoundException
    {
        Conversation conversation = conversations.get(id);
        if (conversation == null)
        {
            throw new ConversationNotFoundException("Conversation with id " + id + " is not created.");
        }
        return conversation;
    }

    /**
     * @inheritDoc
     */
    public void createConversationScenario(int conversationId, String language, String criteria, String transformation)
    {

    }

    /**
     * @inheritDoc
     */
    public void activate(int conversationId) throws ConversationNotFoundException, SimulatorException
    {
        Conversation conversation = getConversationById(conversationId);
        try
        {
            routeManager.activate(conversation);
        }
        catch (Exception e)
        {
            throw new SimulatorException("Conversation activation exception", e);

        }
    }

    /**
     * @inheritDoc
     */
    public void deactivate(int conversationId) throws ConversationNotFoundException, SimulatorException
    {

        Conversation conversation = getConversationById(conversationId);
        try
        {
            routeManager.deactivate(conversation);
        }
        catch (Exception e)
        {
            throw new SimulatorException("Conversation deactivation exception", e);
        }

    }

    /**
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
            //ignore
        }
    }
}
