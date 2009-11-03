package com.tacitknowledge.simulator;

/**
 * @author galo
 */
public interface ConversationManager {
    /**
     * Creates a new Conversation from the provided transports and formats
     * @param inboundTransport
     * @param outboundTransport
     * @param inboundFormat
     * @param outboundFormat
     * @return
     */
    public Conversation createConversation(
            Transport inboundTransport, Transport outboundTransport, String inboundFormat, String outboundFormat) throws UnsupportedFormatException;

    /**
     * Creates a new scenario for an existing conversation
     * @param conversationId
     * @param language The scripting language for the scenario. This would be System wide.
     * @param criteria The criteria script
     * @param transformation The transformation script
     */
    void createConversationScenario(int conversationId, String language, String criteria, String transformation);

    /**
     * 
     * @param conversationId
     */
    void activate(int conversationId);

    /**
     * 
     * @param conversationId
     */
    void deactivate(int conversationId);
}