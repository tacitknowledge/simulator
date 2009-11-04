package com.tacitknowledge.simulator;

import com.tacitknowledge.simulator.camel.RouteManager;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Jorge Galindo (jgalindo@tacitknowledge.com)
 */
public class ConversationManagerImpl implements ConversationManager
{
    /**
     * Singleton instance
     */
    private static ConversationManager _instance;

    /**
     * CamelRoutes manager
     */
    private RouteManager drb;

    /**
     * Currently configured conversations
     */
    private Map<Integer, Conversation> conversations = new HashMap<Integer, Conversation>();

    /**
     * Private Singleton constructor
     * @param drb RouteManager to use
     */
    private ConversationManagerImpl(RouteManager drb)
    {
        this.drb = drb;
    }

    /**
     * Get the singleton instance of the ConversationManager
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
                e.printStackTrace();
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
     * @inheritDoc
     */
    public Conversation getConversationById(int id)
    {
        return conversations.get(id);

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
    public void activate(int conversationId) throws Exception
    {
        Conversation conversation = getConversationById(conversationId);
        drb.activate(conversation);
    }

    /**
     * @inheritDoc
     */
    public void deactivate(int conversationId) throws Exception
    {

        Conversation conversation = getConversationById(conversationId);
        drb.deactivate(conversation);

    }
}
