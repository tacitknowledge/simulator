package com.tacitknowledge.simulator;

/**
 * Factory for creating conversation objects
 *
 * @author Jorge Galindo (jgalindo@tacitknowledge.com)
 */
public class ConversationFactory
{
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
    public static Conversation createConversation(Integer id, Transport inboundTransport,
            Transport outboundTransport, Adapter inboundAdapter, Adapter outboundAdapter)
            throws SimulatorException
    {

        if (inboundAdapter == null || outboundAdapter == null)
        {
            throw new SimulatorException("Both inbound and outbound"
                    + " adapters are required for conversation");
        }
        return new Conversation(id, inboundTransport, outboundTransport, inboundAdapter,
                outboundAdapter);
    }

    /**
     * Hidding the default constructor
     */
    private ConversationFactory()
    {
    }
}
