package com.tacitknowledge.simulator.transports;

import com.tacitknowledge.simulator.Transport;
import com.tacitknowledge.simulator.TransportException;
import com.tacitknowledge.simulator.configuration.ParameterDefinitionBuilder;
import static com.tacitknowledge.simulator.configuration.ParameterDefinitionBuilder.name;
import static com.tacitknowledge.simulator.configuration.ParametersListBuilder.parameters;

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
     * Broker URL parameter. REQUIRED.
     */
    public static final String PARAM_BROKER_URL = "brokerUrl";

    /**
     * JSM broker user name parameter. Optional.
     */
    public static final String PARAM_USER_NAME = "userName";

    /**
     * JSM broker password parameter. Optional
     */
    public static final String PARAM_PASSWORD = "password";

    /**
     * Transport parameters definition.
     */
    private static List<ParameterDefinitionBuilder.ParameterDefinition> parametersList =
        parameters().
            add(
                name(PARAM_DESTINATION_NAME).
                    label("Destination Name").
                    required()
            ).
            add(
                name(PARAM_IS_TOPIC).
                    label("Is the destination a topic (defaults to Queue)").
                    type(ParameterDefinitionBuilder.ParameterDefinition.TYPE_BOOLEAN)
            ).
            add(
                name(PARAM_BROKER_URL).
                    label("Broker URL (e.g. tcp://localhost:61616)").
                    required()
            ).
            add(
                name(PARAM_USER_NAME).
                    label("Broker user name")
            ).
            add(
                name(PARAM_PASSWORD).
                    label("Broker password")
            );

    /**
     * @see #PARAM_ACTIVE_MQ
     */
    private boolean activeMQ = true;

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

        sb.append("?brokerURL=").append(getParamValue(PARAM_BROKER_URL));

        if (getParamValue(PARAM_USER_NAME) != null)
        {
            sb.append(AMP).append("username=").append(getParamValue(PARAM_USER_NAME));
        }
        if (getParamValue(PARAM_PASSWORD) != null)
        {
            sb.append(AMP).append("password=").append(getParamValue(PARAM_PASSWORD));
        }

        return sb.toString();
    }

    /**
     * @return List of Parameters for File Transport.
     * @inheritDoc
     */
    public List<List> getParametersList()
    {
        return getParametersDefinitionsAsList(parametersList);
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
        if (getParamValue(PARAM_BROKER_URL) == null)
        {
            throw new TransportException("Broker URL is required.");
        }
    }
}
