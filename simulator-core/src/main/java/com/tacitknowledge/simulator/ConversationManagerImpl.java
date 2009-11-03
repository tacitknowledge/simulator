package com.tacitknowledge.simulator;

/**
 * @author galo
 */
public class ConversationManagerImpl implements ConversationManager {

    private static ConversationManager instance = new ConversationManagerImpl();

    public static ConversationManager getInstance() {
        return instance;
    }

    public Conversation createConversation(Transport inboundTransport, Transport outboundTransport, String inboundFormat, String outboundFormat) throws UnsupportedFormatException {
        Adapter inAdapter = AdapterFactory.getAdapter(inboundFormat);
        Adapter outAdapter = AdapterFactory.getAdapter(inboundFormat);
         if(inAdapter==null||outAdapter==null) throw new UnsupportedFormatException();

        return new Conversation(inboundTransport, outboundTransport, inAdapter, outAdapter);
    }

    public void createConversationScenario(int conversationId, String language, String criteria, String transformation) {

    }

    public void activate(int conversationId) {

    }

    public void deactivate(int conversationId) {

    }
}
