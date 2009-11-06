package com.tacitknowledge.simulator;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

/**
 * The Simulator conversation as set up by the user. Works as a wrapper around Camel route
 * definition for entry and exit endpoints.
 *
 * @author Jorge Galindo (jgalindo@tacitknowledge.com)
 */
public class Conversation
{
    /**
     * Logger for this class.
     */
    private static Logger logger = Logger.getLogger(Conversation.class);
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
    private List<ConversationScenario> scenarios;

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
    public Conversation(Integer id, Transport inboundTransport, Transport outboundTransport,
            Adapter inboundAdapter, Adapter outboundAdapter)
    {
        this.id = id;
        this.inboundTransport = inboundTransport;
        this.outboundTransport = outboundTransport;
        this.inboundAdapter = inboundAdapter;
        this.outboundAdapter = outboundAdapter;
        this.scenarios = new ArrayList<ConversationScenario>();
    }

    /**
     * Adds a new Scenario to this Conversation
     *
     * @param language
     *            The scripting language to be used.
     * @param criteria criteria script to be executed by the simulator
     * @param transformation transformation script to be executed by the simulator
     * @return ConversationScenario conversation scenario added to the conversation
     */
    public ConversationScenario addScenario(String language, String criteria, String transformation)
    {
        ConversationScenario conversationScenario
                    = new ConversationScenario(language, criteria, transformation);

        if (this.scenarios == null)
        {
            this.scenarios = new ArrayList<ConversationScenario>();
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
     * Returns this conversation inbound transport
     *
     * @return inbound transport
     */
    public Transport getInboundTransport()
    {
        return inboundTransport;
    }

    /**
     * Returns this conversation outbound transport
     *
     * @return outbound transport
     */
    public Transport getOutboundTransport()
    {
        return outboundTransport;
    }

    /**
     * Retuns this conversation inbound format adapter
     *
     * @return inbound adapter
     */
    public Adapter getInboundAdapter()
    {
        return inboundAdapter;
    }

    /**
     * Retuns this conversation outbound format adapter
     *
     * @return outbound adapter
     */
    public Adapter getOutboundAdapter()
    {
        return outboundAdapter;
    }

    /**
     * Gets the unique id of the conversation based on the parameters provided
     * on object construction. Needed to ensure that RouteManager doesn't add
     * two identic routes to camel.
     * @return unique id based on the parameters of the object.
     */
    public String getUniqueId()
    {
        StringBuffer sb = new StringBuffer();

        sb.append(id.toString()).append("|").
            append(getInboundTransport().toUriString()).append("|").
                append(getInboundAdapter().getClass().getName()).append("|").
                    append(getOutboundAdapter().getClass().getName()).append("|").
                        append(getOutboundTransport().toUriString());

        return sb.toString();
    }

    /**
     * Returns the current list of configured scenarios for this conversation
     *
     * @return a list of ConversationScenarios
     */
    public List<ConversationScenario> getScenarios()
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

        Conversation that = (Conversation) o;

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
