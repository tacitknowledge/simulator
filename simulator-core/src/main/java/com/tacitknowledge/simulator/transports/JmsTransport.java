package com.tacitknowledge.simulator.transports;

import com.tacitknowledge.simulator.Transport;
import com.tacitknowledge.simulator.TransportException;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Transport implementation for Jms endpoints.
 *
 * @author Jorge Galindo (jgalindo@tacitknowledge.com)
 */
public class JmsTransport extends BaseTransport implements Transport
{
    /**
     * Active MQ parameter. Determines if JMS is Apache ActiveMQ (true) or generic JMS (false).
     * OPTIONAL. Defaults to false (generic JMS)
     */
    public static final String PARAM_ACTIVE_MQ = "activeMQ";

    /**
     * JMS Destination name parameter. REQUIRED.
     */
    public static final String PARAM_DESTINATION_NAME = "destinationName";

    /**
     * JMS topic name parameter. OPTIONAL.
     */
    public static final String PARAM_IS_TOPIC = "isTopic";

    /**
     * Transport parameters definition.
     */
    private static List<List> parametersList = new ArrayList<List>()
    {
        {
            add(new ArrayList<String>()
            {
                {
                    add(PARAM_ACTIVE_MQ);
                    add("Is this JMS an Apache ActiveMQ implementation? (defaults to no)");
                    add("boolean");
                    add("optional");
                }
            });

            add(new ArrayList<String>()
            {
                {
                    add(PARAM_DESTINATION_NAME);
                    add("Destination Name");
                    add("string");
                    add("required");
                }
            });

            add(new ArrayList<String>()
            {
                {
                    add(PARAM_IS_TOPIC);
                    add("Is the destination a topic (defaults to Queue)");
                    add("boolean");
                    add("optional");
                }
            });
        }
    };

    /**
     * @see #PARAM_ACTIVE_MQ
     */
    private boolean activeMQ = false;

    /**
     * @see #PARAM_IS_TOPIC
     */
    private boolean isTopic = false;

    /**
     * @inheritDoc
     */
    public JmsTransport()
    {
        super(TransportConstants.JMS);
    }

    /**
     * @param parameters @see #parameters
     * @inheritDoc
     */
    public JmsTransport(Map<String, String> parameters)
    {
        super(TransportConstants.JMS, parameters);
    }

    /**
     * @return @see #Transport.toUriString()
     * @throws TransportException If a required parameter is missing or not properly formatted.
     * @inheritDoc
     */
    public String toUriString() throws TransportException
    {
        validateParameters();

        StringBuilder sb = new StringBuilder();

        // --- Check JMS type
        if (this.activeMQ)
        {
            sb.append("activemq");
        }
        else
        {
            sb.append("jms");
        }
        sb.append(":");

        // --- if destination is a topic, add the topic prefix, otherwise keep going
        if (this.isTopic)
        {
            sb.append("topic:");
        }

        sb.append(getParamValue(PARAM_DESTINATION_NAME));

        return sb.toString();
    }

    /**
     * @return List of Parameters for File Transport.
     * @inheritDoc
     */
    public List<List> getParametersList()
    {
        return parametersList;
    }

    /**
     * @throws TransportException If any required parameter is missing or incorrect
     * @inheritDoc
     */
    @Override
    void validateParameters() throws TransportException
    {
        if (getParamValue(PARAM_ACTIVE_MQ) != null)
        {
            this.activeMQ = Boolean.parseBoolean(getParamValue(PARAM_ACTIVE_MQ));
        }
        if (getParamValue(PARAM_IS_TOPIC) != null)
        {
            this.isTopic = Boolean.parseBoolean(getParamValue(PARAM_IS_TOPIC));
        }

        if (getParamValue(PARAM_DESTINATION_NAME) == null)
        {
            throw new TransportException("Destination name parameter is required");
        }
    }
}
