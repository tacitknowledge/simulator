package com.tacitknowledge.simulator.impl;

import com.tacitknowledge.simulator.Adapter;
import com.tacitknowledge.simulator.Conversation;
import com.tacitknowledge.simulator.SimulatorException;
import com.tacitknowledge.simulator.Transport;

/**
 * Factory for creating conversation objects
 *
 * @author Jorge Galindo (jgalindo@tacitknowledge.com)
 */
public final class ConversationFactory
{
    /**
     * Hiding the default constructor
     */
    private ConversationFactory()
    {
    }

    /**
     * Creates a new Conversation from the given transports and adapters.
     *
     * @param id              conversation id
     * @param inboundTransport  inbound transport
     * @param outboundTransport outbound transport
     * @param inboundAdapter    inbound adapter
     * @param outboundAdapter   outbound adapter
     * @return The created Conversation
     */
    public static Conversation createConversation(
        final String id,
        final Transport inboundTransport,
        final Transport outboundTransport,
        final Adapter inboundAdapter,
        final Adapter outboundAdapter)
    {
        if (inboundAdapter == null || outboundAdapter == null
                || inboundTransport == null
                || outboundTransport == null)
        {
            String errorMessage = "Inbound and outbound"
                    + " adapters and transports are required for creating new conversation.";

            throw new IllegalArgumentException(errorMessage);
        }

        return new ConversationImpl(id, inboundTransport, outboundTransport, inboundAdapter, outboundAdapter);
    }
}
