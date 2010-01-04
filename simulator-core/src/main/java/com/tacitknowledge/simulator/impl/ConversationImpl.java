package com.tacitknowledge.simulator.impl;

import com.tacitknowledge.simulator.Adapter;
import com.tacitknowledge.simulator.Conversation;
import com.tacitknowledge.simulator.ConversationScenario;
import com.tacitknowledge.simulator.Transport;
import org.apache.log4j.Logger;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * The Simulator conversation as set up by the user. Works as a wrapper around Camel route
 * definition for entry and exit endpoints.
 *
 * @author Jorge Galindo (jgalindo@tacitknowledge.com)
 */
public class ConversationImpl implements Conversation
{
    /**
     * Logger for this class.
     */
    private static Logger logger = Logger.getLogger(ConversationImpl.class);
    /**
     * Prime number to be used in the hashcode method.
     */
    private static final int HASH_CODE_PRIME = 31;

    /**
     * Conversation ID.
     */
    private Integer id;

    /**
     * Conversation Name.
     */
    private String name;
    /**
     * Wrapper for inbound transport configuration
     */
    private Transport inboundTransport;

    /**
     * Wrapper for outbound transport configuration
     */
    private Transport outboundTransport;

    /**
     * Wrapper for inbound format adapter
     */
    private Adapter inboundAdapter;

    /**
     * Wrapper for outbound format adapter
     */
    private Adapter outboundAdapter;

    /**
     * Default response
     */
    private String defaultResponse;

    /**
     * List of configured scenarios for this conversation
     */
    private Map<Integer, ConversationScenario> scenarios
        = new HashMap<Integer, ConversationScenario>();

    /**
     * Constructor
     *
     * @param id                Conversation ID
     * @param name              Conversation name
     * @param inboundTransport  Wrapper for inbound transport configuration
     * @param outboundTransport Wrapper for outbound transport configuration
     * @param inboundAdapter    Wrapper for inbound format adapter
     * @param outboundAdapter   Outbound adapter
     * @param defaultResponse   Default response
     */
    public ConversationImpl(final Integer id, final String name, final Transport inboundTransport,
                            final Transport outboundTransport, final Adapter inboundAdapter,
                            final Adapter outboundAdapter, final String defaultResponse)
    {
        this.id = id;
        this.name = name;
        this.inboundTransport = inboundTransport;
        this.outboundTransport = outboundTransport;
        this.inboundAdapter = inboundAdapter;
        this.outboundAdapter = outboundAdapter;
        this.defaultResponse = defaultResponse;
    }

    /**
     * {@inheritDoc}
     */
    public ConversationScenario addOrUpdateScenario(final int scenarioId,
                                                    final String language,
                                                    final String criteria,
                                                    final String transformation)
    {
        ConversationScenario scenario = scenarios.get(scenarioId);
        if (scenario == null)
        {

            scenario = new ConversationScenarioImpl(scenarioId, language, criteria, transformation);
            scenarios.put(scenarioId, scenario);
            logger.info("Added new conversation scenario"
                + " to the conversation with id : " + this.id);
        }
        else
        {
            scenario.setScripts(criteria, transformation, language);
            logger.info("Updated conversation scenario with id " + scenarioId
                + " to the conversation with id : " + this.id);
        }

        return scenario;
    }

    /**
     * {@inheritDoc}
     */
    public Transport getInboundTransport()
    {
        return inboundTransport;
    }

    /**
     * {@inheritDoc}
     */
    public Transport getOutboundTransport()
    {
        return outboundTransport;
    }

    /**
     * {@inheritDoc}
     */
    public Adapter getInboundAdapter()
    {
        return inboundAdapter;
    }

    /**
     * {@inheritDoc}
     */
    public Adapter getOutboundAdapter()
    {
        return outboundAdapter;
    }

    /**
     * {@inheritDoc}
     */
    public String getDefaultResponse()
    {
        return defaultResponse;
    }

    /**
     * Gets the conversation ID
     *
     * @return the conversation ID
     */
    public int getId()
    {
        return id;
    }

    /**
     * Gets the conversation Name
     *
     * @return the conversation name
     */
    public String getName()
    {
        return name;
    }

    /**
     * {@inheritDoc}
     */
    public Collection<ConversationScenario> getScenarios()
    {
        return scenarios.values();
    }

    /**
     * Determines if the current conversation object is equal to the one supplied as parameter.
     *
     * @param o Conversation object to be compared with current one.
     * @return true if the objects are considered equal, false otherwise
     */
    public boolean equals(final Object o)
    {
        if (this == o)
        {
            return true;
        }

        ConversationImpl that = (ConversationImpl) o;

        if (o == null 
            || getClass() != o.getClass()
            || !id.equals(that.id)
            || !inboundAdapter.equals(that.inboundAdapter)
            || !inboundTransport.equals(that.inboundTransport)
            || !outboundAdapter.equals(that.outboundAdapter)
            || !outboundTransport.equals(that.outboundTransport))
        {
            return false;
        }

        return scenarios.equals(that.scenarios);
    }

    /**
     * Override for the hashcode.
     *
     * @return hashcode
     */
    public int hashCode()
    {
        int result = id.hashCode();
        result = HASH_CODE_PRIME * result + inboundTransport.hashCode();
        result = HASH_CODE_PRIME * result + outboundTransport.hashCode();
        result = HASH_CODE_PRIME * result + inboundAdapter.hashCode();
        result = HASH_CODE_PRIME * result + outboundAdapter.hashCode();
        return result;
    }

    @Override
    public String toString()
    {
        return "ConversationImpl{"
            + "id="
            + id
            + ", inboundTransport="
            + inboundTransport
            + ", outboundTransport="
            + outboundTransport
            + ", inboundAdapter="
            + inboundAdapter
            + ", outboundAdapter="
            + outboundAdapter
            + ", defaultResponse='"
            + defaultResponse
            + '\''
            + ", scenarios="
            + scenarios
            + '}';
    }

}
