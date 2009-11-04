package com.tacitknowledge.simulator;

import java.util.List;

/**
 * The Simulator conversation as set up by the user.
 * Works as a wrapper arround Camel route definition for entry and exit endpoints.
 *
 * @author Jorge Galindo (jgalindo@tacitknowledge.com)
 */
public class Conversation
{
    private Integer id;
    /**
     *
     */
    private Transport inboundTransport;
    /**
     *
     */
    private Transport outboundTransport;
    /**
     *
     */
    private Adapter inboundAdapter;
    /**
     *
     */
    private Adapter outboundAdapter;

    /**
     * List of configured scenarios for this conversation
     */
    private List<ConversationScenario> scenarios;

    public Conversation(Integer id, Transport inboundTransport, Transport outboundTransport, Adapter inboundAdapter, Adapter outboundAdapter)
    {
        this.id = id;
        this.inboundTransport = inboundTransport;
        this.outboundTransport = outboundTransport;
        this.inboundAdapter = inboundAdapter;
        this.outboundAdapter = outboundAdapter;
    }

    public ConversationScenario addScenario(String language, String criteria, String transformation)
    {
        return null;
    }

    public Transport getInboundTransport()
    {
        return inboundTransport;
    }

    public Transport getOutboundTransport()
    {
        return outboundTransport;
    }

    public Adapter getInboundAdapter()
    {
        return inboundAdapter;
    }

    public Adapter getOutboundAdapter()
    {
        return outboundAdapter;
    }

    public List<ConversationScenario> getScenarios()
    {
        return scenarios;
    }
}
