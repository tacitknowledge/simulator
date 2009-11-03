package com.tacitknowledge.simulator;

import com.tacitknowledge.simulator.adapters.Adapter;

/**
 * @author galo
 */
public class ConversationFactory {
    /**
     * Creates a new Conversation from the given transports and adapters.
     * @param inboundTransport
     * @param outboundTransport
     * @param inboundAdapter
     * @param outboundAdapter
     * @return
     */
    public Conversation createConversation(
            Transport inboundTransport, Transport outboundTransport, Adapter inboundAdapter, Adapter outboundAdapter) {
        return null;
    }
}
