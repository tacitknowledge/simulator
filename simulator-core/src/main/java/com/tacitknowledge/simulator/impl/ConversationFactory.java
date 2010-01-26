package com.tacitknowledge.simulator.impl;

import com.tacitknowledge.simulator.Adapter;
import com.tacitknowledge.simulator.SimulatorException;
import com.tacitknowledge.simulator.Transport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Factory for creating conversation objects
 *
 * @author Jorge Galindo (jgalindo@tacitknowledge.com)
 */
public final class ConversationFactory
{
    /**
     * Logger for this class.
     */
    private static Logger logger = LoggerFactory.getLogger(ConversationFactory.class);


    /**
     * Hiding the default constructor
     */
    private ConversationFactory()
    {
    }

    /**
     * Creates a new Conversation from the given transports and adapters.
     *
     * @param id                the id of the conversation.
     * @param name              conversation name
     * @param inboundTransport  inbound transport
     * @param outboundTransport outbound transport
     * @param inboundAdapter    inbound adapter
     * @param outboundAdapter   outbound adapter
     * @param defaultResponse   @return conversation object
     * @return The created Conversation
     * @throws SimulatorException in case of an error
     */
    public static ConversationImpl createConversation(
        final Integer id, final String name,
        final Transport inboundTransport,
        final Transport outboundTransport,
        final Adapter inboundAdapter,
        final Adapter outboundAdapter,
        final String defaultResponse)
        throws SimulatorException
    {
        if (inboundAdapter == null || outboundAdapter == null
                || inboundTransport == null
                || outboundTransport == null)
        {
            String errorMessage = "Inbound and outbound"
                    + " adapters and transports are required for creating new conversation.";

            throw new SimulatorException(errorMessage);
        }
        return new ConversationImpl(id, name, inboundTransport, outboundTransport, inboundAdapter,
                outboundAdapter, defaultResponse);
    }
}
