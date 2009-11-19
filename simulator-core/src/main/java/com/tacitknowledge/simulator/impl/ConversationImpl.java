package com.tacitknowledge.simulator.impl;

import java.util.ArrayList;
import java.util.List;

import com.tacitknowledge.simulator.Adapter;
import com.tacitknowledge.simulator.Conversation;
import com.tacitknowledge.simulator.ConversationScenario;
import com.tacitknowledge.simulator.Transport;
import com.tacitknowledge.simulator.SimulatorException;
import com.tacitknowledge.simulator.TransportException;
import org.apache.log4j.Logger;

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
     * List of configured scenarios for this conversation
     */
    private List<ConversationScenarioImpl> scenarios;

    /**
     * @param id
     *            Conversation ID
     * @param inboundTransport
     *            Wrapper for inbound transport configuration
     * @param outboundTransport
     *            Wrapper for outbound transport configuration
     * @param inboundAdapter
     *            Wrapper for inbound format adapter
     * @param outboundAdapter
     *            Wrapper for outbound format adapter
     */
    public ConversationImpl(Integer id, Transport inboundTransport, Transport outboundTransport,
            Adapter inboundAdapter, Adapter outboundAdapter)
    {
        this.id = id;
        this.inboundTransport = inboundTransport;
        this.outboundTransport = outboundTransport;
        this.inboundAdapter = inboundAdapter;
        this.outboundAdapter = outboundAdapter;
        this.scenarios = new ArrayList<ConversationScenarioImpl>();
    }

    /**
     * {@inheritDoc}
     */
    public ConversationScenario addScenario(String language, String criteria, String transformation)
    {
        ConversationScenarioImpl conversationScenario
                    = new ConversationScenarioImpl(language, criteria, transformation);

        if (this.scenarios == null)
        {
            this.scenarios = new ArrayList<ConversationScenarioImpl>();
        }

        if (!scenarios.contains(conversationScenario))
        {
            scenarios.add(conversationScenario);

            logger.debug("Added new conversation scenario"
                    + " to the conversation with id : " + this.id);
        }

        return conversationScenario;
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
    public String getUniqueId() throws SimulatorException
    {
        StringBuffer sb = new StringBuffer();

        try
        {
            sb.append(id.toString()).append("|").
                append(getInboundTransport().toUriString()).append("|").
                    append(getOutboundTransport().toUriString());
        }
        catch (TransportException te)
        {
            logger.error("Unexpected error trying to get unique Id: " + te.getMessage());
            throw new SimulatorException(
                    "Unexpected error trying to get unique Id: " + te.getMessage(), te);
        }

        return sb.toString();
    }

    /**
     * {@inheritDoc}
     */
    public List<ConversationScenarioImpl> getScenarios()
    {
        return scenarios;
    }

    /**
     * Determines if the current conversation object is equal to the one supplied as parameter.
     * @param o Conversation object to be compared with current one.
     * @return true if the objects are considered equal, false otherwise
     */
    public boolean equals(Object o)
    {
        if (this == o)
        {
            return true;
        }
        if (o == null || getClass() != o.getClass())
        {
            return false;
        }

        ConversationImpl that = (ConversationImpl) o;

        if (!id.equals(that.id))
        {
            return false;
        }
        if (!inboundAdapter.equals(that.inboundAdapter))
        {
            return false;
        }
        if (!inboundTransport.equals(that.inboundTransport))
        {
            return false;
        }
        if (!outboundAdapter.equals(that.outboundAdapter))
        {
            return false;
        }
        if (!outboundTransport.equals(that.outboundTransport))
        {
            return false;
        }
        if (!scenarios.equals(that.scenarios))
        {
            return false;
        }

        return true;
    }

    /**
     * Override for the hashcode.
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
}
