package com.tacitknowledge.simulator.impl;

import com.tacitknowledge.simulator.Adapter;
import com.tacitknowledge.simulator.Conversation;
import com.tacitknowledge.simulator.Transport;

/**
 * Factory for creating conversation objects
 *
 * @author Jorge Galindo (jgalindo@tacitknowledge.com)
 */
public class ConversationFactory {

    /**
     * Creates a new Conversation from the given transports and adapters.  This will wrap the Conversation
     * in a Proxy, which may degrade the process(exchange) method if configured.
     *
     * @param conversationPath  path to conversation dir, also used as an id
     * @param inboundTransport  inbound transport
     * @param outboundTransport outbound transport
     * @param inboundAdapter    inbound adapter
     * @param outboundAdapter   outbound adapter
     * @return The created Conversation
     */
    public Conversation createConversation(final String conversationPath, final Transport inboundTransport,
            final Transport outboundTransport, final Adapter inboundAdapter, final Adapter outboundAdapter) {

        final boolean allParamsProvided = allParamsProvided(inboundAdapter, outboundAdapter,
                inboundTransport, outboundTransport);

        if (allParamsProvided)
        {
            return new ConversationImpl(conversationPath, inboundTransport, outboundTransport,
                inboundAdapter, outboundAdapter);
        }
        else
        {
            final String errorMessage = getErrorMessage(inboundAdapter, outboundAdapter,
                    inboundTransport, outboundTransport);
            throw new IllegalArgumentException(errorMessage);
        }
    }

    protected boolean allParamsProvided(final Adapter inboundAdapter, final Adapter outboundAdapter,
        final Transport inboundTransport, final Transport outboundTransport)
    {
        return  inboundAdapter != null && outboundAdapter != null && inboundTransport != null &&
                outboundTransport != null;
    }

    protected String getErrorMessage(final Adapter inboundAdapter, final Adapter outboundAdapter,
        final Transport inboundTransport, final Transport outboundTransport)
    {
        final StringBuilder stringBuilder = new StringBuilder().

                append("Inbound and outbound ").
                append("adapters and transports are required for creating new conversation. ").
                append("ITransport=").append(inboundTransport).
                append(", OTransport=").append(outboundTransport).
                append(", IAdapter=").append(inboundAdapter).
                append(", OAdapter=").append(outboundAdapter);

        return stringBuilder.toString();
    }
}
