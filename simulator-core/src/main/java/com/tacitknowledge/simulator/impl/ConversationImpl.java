package com.tacitknowledge.simulator.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tacitknowledge.simulator.Adapter;
import com.tacitknowledge.simulator.Conversation;
import com.tacitknowledge.simulator.ConversationScenario;
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
     * Logger for this class.
     */
    private static Logger logger = LoggerFactory.getLogger(ConversationImpl.class);

    /**
     * Prime number to be used in the hashcode method.
     */
    private static final int HASH_CODE_PRIME = 31;

    /**
     * Conversation configuration directory (as identifier)
     */
    private String conversationPath;
    
    private ConversationScenarioFactory scenarioFactory;

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
    private List<ConversationScenario> scenarios = new ArrayList<ConversationScenario>();

    public ConversationImpl(final String conversationPath,
                            final ConversationScenarioFactory scenarioFactory,
                            final Transport inboundTransport,
                            final Transport outboundTransport,
                            final Adapter inboundAdapter,
                            final Adapter outboundAdapter)
    {
        this.conversationPath = conversationPath;
        this.scenarioFactory = scenarioFactory;
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
    public ConversationScenario addScenario(final String scenarioConfigFilePath,
            final String language, final String criteria, final String transformation)
    {
        ConversationScenario scenario = scenarioFactory.createConversationScenario(
                scenarioConfigFilePath, language, criteria, transformation);
        scenarios.add(scenario);

        logger.info("Added new conversation scenario to the conversation located at : {}",
                this.conversationPath);
        return scenario;
    }

    /**
     * {@inheritDoc}
     */
    public void addScenario(ConversationScenario scenario)
    {
        scenarios.add(scenario);
        logger.info("Added new conversation scenario to the conversation located at : {}",
                this.conversationPath);
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
    public Map<String, ConversationScenario> getScenarios()
    {
        Map<String, ConversationScenario> map = new HashMap<String, ConversationScenario>();
        
        for (ConversationScenario scenario : scenarios)
        {
            map.put(scenario.getConfigurationFilePath(), scenario);
        }
        
        return Collections.unmodifiableMap(map);
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
