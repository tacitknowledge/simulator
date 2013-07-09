package com.tacitknowledge.simulator.impl;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.apache.camel.Exchange;

import com.tacitknowledge.simulator.Adapter;
import com.tacitknowledge.simulator.Conversation;
import com.tacitknowledge.simulator.Scenario;
import com.tacitknowledge.simulator.Transport;
import com.tacitknowledge.simulator.utils.ConversationUtil;

/**
 * The Simulator conversation as set up by the user. Works as a wrapper around Camel route
 * definition for entry and exit endpoints.
 *
 * @author Jorge Galindo (jgalindo@tacitknowledge.com)
 */
public class ConversationImpl implements Conversation
{
    /**
     * Prime number to be used in the hashcode method.
     */
    private static final int HASH_CODE_PRIME = 31;

    /**
     * Conversation configuration directory (as identifier)
     */
    private String conversationPath;
    
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
     * Last modified date of inbound.properties file
     */
    private long inboundModifiedDate;

    /**
     * Last modified date of outbound.properties file
     */
    private long outboundModifiedDate;

    /**
     * List of configured scenarios for this conversation
     */
    private Map<String, Scenario> scenarios = new HashMap<String, Scenario>();

    public ConversationImpl(final String conversationPath,
                            final Transport inboundTransport,
                            final Transport outboundTransport,
                            final Adapter inboundAdapter,
                            final Adapter outboundAdapter)
    {
        this.conversationPath = conversationPath;
        this.inboundTransport = inboundTransport;
        this.outboundTransport = outboundTransport;
        this.inboundAdapter = inboundAdapter;
        this.outboundAdapter = outboundAdapter;
        this.inboundModifiedDate = ConversationUtil.getFileModifiedDate(conversationPath,
                Conversation.INBOUND_CONFIG);
        this.outboundModifiedDate = ConversationUtil.getFileModifiedDate(conversationPath,
                Conversation.OUTBOUND_CONFIG);
    }

    /**
     * {@inheritDoc}
     */
    public void addScenario(Scenario scenario)
    {
        scenarios.put(scenario.getConfigurationFilePath(), scenario);        
    }
    
    public void process(final Exchange exchange) throws Exception
    {
        Map<String, Object> scriptExecutionBeans = inboundAdapter.adaptForInput(exchange);
        Object result = null;
        
        for (Scenario scenario : scenarios.values())
        {
            boolean matchesCondition = scenario.matchesCondition(scriptExecutionBeans);
            
            
            if (matchesCondition)
            {
                result = scenario.executeTransformation(scriptExecutionBeans);
                break;
            }
        }

        //result seems odd
        Object exchangeBody = outboundAdapter.adaptToOutput(result, exchange);
        exchange.getOut().setBody(exchangeBody);
    }


    /**
     * {@inheritDoc}
     */
    public long getIboundModifiedDate()
    {
        return this.inboundModifiedDate;
    }

    /**
     * {@inheritDoc}
     */
    public long getOutboundModifiedDate()
    {
        return this.outboundModifiedDate;
    }

    /**
     * {@inheritDoc}
     */
    public String getId()
    {
        return conversationPath;
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
    public Map<String, Scenario> getScenarios()
    {       
        return Collections.unmodifiableMap(scenarios);
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

        if (o == null || getClass() != o.getClass()
                || !conversationPath.equals(that.conversationPath)
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
        int result = conversationPath.hashCode();
        result = HASH_CODE_PRIME * result + inboundTransport.hashCode();
        result = HASH_CODE_PRIME * result + outboundTransport.hashCode();
        result = HASH_CODE_PRIME * result + inboundAdapter.hashCode();
        result = HASH_CODE_PRIME * result + outboundAdapter.hashCode();
        return result;
    }

    @Override
    public String toString()
    {
        return "ConversationImpl{" + "conversationPath=" + conversationPath + ", inboundTransport="
                + inboundTransport + ", outboundTransport=" + outboundTransport
                + ", inboundAdapter=" + inboundAdapter + ", outboundAdapter=" + outboundAdapter
                + ", scenarios=" + scenarios + '}';
    }
}
