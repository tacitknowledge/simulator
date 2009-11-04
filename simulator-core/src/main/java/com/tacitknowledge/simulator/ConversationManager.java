package com.tacitknowledge.simulator;

import com.tacitknowledge.simulator.transports.Transport;

/**
 * Defines the interface for the implementations of the ConversationManager.
 *
 * @author Jorge Galindo (jgalindo@tacitknowledge.com)
 */
public interface ConversationManager
{
    /**
     * Creates a new Conversation from the provided transports and formats
     *
     * @param id
     * @param inboundTransport
     * @param outboundTransport
     * @param inboundFormat
     * @param outboundFormat
     * @return
     */
    public Conversation createConversation(
            Integer id, Transport inboundTransport, Transport outboundTransport, String inboundFormat, String outboundFormat) throws UnsupportedFormatException;

    /**
     * Creates a new scenario for an existing conversation
     *
     * @param conversationId
     * @param language       The scripting language for the scenario. This would be System wide.
     * @param criteria       The criteria script
     * @param transformation The transformation script
     */
    void createConversationScenario(int conversationId, String language, String criteria,
                                    String transformation);

    /**
     * Activates the conversation with provided conversation id.
     *
     * @param conversationId id of the conversation to be activated.
     */
    void activate(int conversationId) throws Exception;

    /**
     * Deactivates the conversation with provided conversation id.
     *
     * @param conversationId id of the conversation to be deactivated.
     */
    void deactivate(int conversationId) throws Exception;

    /**
     * @param conversationId
     * @return existing&running conversation. otherwise - null
     */
    Conversation getConversationById(int conversationId);
}