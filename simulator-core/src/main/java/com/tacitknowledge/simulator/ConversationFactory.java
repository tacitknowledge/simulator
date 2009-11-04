package com.tacitknowledge.simulator;

/**
 * @author Jorge Galindo (jgalindo@tacitknowledge.com)
 */
public class ConversationFactory
{
    /**
     * Creates a new Conversation from the given transports and adapters.
     *
     * @param id
     * @param inboundTransport
     * @param outboundTransport
     * @param inboundAdapter
     * @param outboundAdapter   @return
     */
    public static Conversation createConversation(
            Integer id, Transport inboundTransport, Transport outboundTransport, Adapter inboundAdapter, Adapter outboundAdapter) throws UnsupportedFormatException
    {

        if (inboundAdapter == null || outboundAdapter == null)
        {
            throw new UnsupportedFormatException();
        }
        return new Conversation(id, inboundTransport, outboundTransport, inboundAdapter, outboundAdapter);
    }
}
