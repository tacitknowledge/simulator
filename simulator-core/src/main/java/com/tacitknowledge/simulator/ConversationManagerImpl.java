package com.tacitknowledge.simulator;

import com.tacitknowledge.simulator.camel.RouteManager;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Jorge Galindo (jgalindo@tacitknowledge.com)
 */
public class ConversationManagerImpl implements ConversationManager
{

    private static ConversationManager instance;

    private RouteManager drb;
    private Map<Integer, Conversation> conversations = new HashMap<Integer, Conversation>();


    public ConversationManagerImpl(RouteManager drb)
    {
        this.drb = drb;
    }

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
                e.printStackTrace();
            }
        }
        return instance;
    }

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

    public Conversation getConversationById(int id)
    {
        return conversations.get(id);

    }

    public void createConversationScenario(int conversationId, String language, String criteria, String transformation)
    {

    }

    public void activate(int conversationId) throws Exception
    {
        Conversation conversation = getConversationById(conversationId);
        drb.activate(conversation);
    }

    public void deactivate(int conversationId) throws Exception
    {

        Conversation conversation = getConversationById(conversationId);
        drb.deactivate(conversation);

    }
}
