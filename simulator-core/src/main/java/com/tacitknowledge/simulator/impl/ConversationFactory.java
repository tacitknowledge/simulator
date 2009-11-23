package com.tacitknowledge.simulator.impl;

import org.apache.log4j.Logger;

import com.tacitknowledge.simulator.Adapter;
import com.tacitknowledge.simulator.SimulatorException;
import com.tacitknowledge.simulator.Transport;

/**
 * Factory for creating conversation objects
 *
 * @author Jorge Galindo (jgalindo@tacitknowledge.com)
 */
public class ConversationFactory
{
    /**
     * Logger for this class.
     */
    private static Logger logger = Logger.getLogger(ConversationFactory.class);
    /**
     * Creates a new Conversation from the given transports and adapters.
     *
     * @param id the id of the conversation.
     * @param inboundTransport inbound transport
     * @param outboundTransport outbound transport
     * @param inboundAdapter inbound adapter
     * @param outboundAdapter outbound adapter
     * @return conversation object
     * @throws SimulatorException in case of an error
     */
    public static ConversationImpl createConversation(Integer id, Transport inboundTransport,
            Transport outboundTransport, Adapter inboundAdapter, Adapter outboundAdapter)
            throws SimulatorException
    {

        if (inboundAdapter == null || outboundAdapter == null
                || inboundTransport == null
                || outboundTransport == null)
        {
            String errorMessage = "Inbound and outbound"
                + " adapters and transports are required for creating new conversation.";

            logger.error(errorMessage);
            throw new SimulatorException(errorMessage);
        }
        return new ConversationImpl(id, inboundTransport, outboundTransport, inboundAdapter,
                outboundAdapter);
    }

    /**
     * Hidding the default constructor
     */
    private ConversationFactory()
    {
    }
}
