package com.tacitknowledge.simulator.transports;

import com.tacitknowledge.simulator.ConfigurableException;
import com.tacitknowledge.simulator.Transport;
import com.tacitknowledge.simulator.TransportException;
import com.tacitknowledge.simulator.configuration.ParameterDefinitionBuilder;
import static com.tacitknowledge.simulator.configuration.ParameterDefinitionBuilder.name;
import static com.tacitknowledge.simulator.configuration.ParametersListBuilder.parameters;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
     * Logger for this class.
     */
    private static Logger logger = LoggerFactory.getLogger(HttpTransport.class);
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
     * @inheritDoc
     * @param parameters parameters
     */
    public JmsTransport(final Map<String, String> parameters)
    {
        super(TransportConstants.JMS, parameters);
    }

    /**
     * @param parameters @see #parameters
     * @param bound in or out
     * @inheritDoc
     */
    public JmsTransport(final int bound, final Map<String, String> parameters)
    {
        super(bound, TransportConstants.JMS, parameters);
    }

    /**
     * @return @see #Transport.toUriString()
     * @throws TransportException If a required parameter is missing or not properly formatted.
     * @inheritDoc
     */
    public String toUriString() throws ConfigurableException, TransportException
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

        logger.info("Uri String: {}", sb.toString());

        return sb.toString();
    }

    /**
     * @throws ConfigurableException If any required parameter is missing or incorrect
     * @inheritDoc
     */
    @Override
    protected void validateParameters() throws ConfigurableException
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
            throw new ConfigurableException("Destination name parameter is required");
        }
        if (getParamValue(PARAM_BROKER_URL) == null)
        {
            throw new ConfigurableException("Broker URL is required.");
        }
    }

    /**
     * Returns a List of parameters the implementing instance uses.
     * Each list element is itself a List to describe the parameter as follows:
     * <p/>
     * - 0 : Parameter name
     * - 1 : Parameter description. Useful for GUI rendition
     * - 2 : Parameter type. Useful for GUI rendition.
     * - 3 : Required or Optional parameter. Useful for GUI validation.
     * - 4 : Parameter usage. Useful for GUI rendition.
     * - 5 : Default value
     *
     * @return List of Parameters for the implementing Transport.
     * @see com.tacitknowledge.simulator.configuration.ParameterDefinitionBuilder
     * @see com.tacitknowledge.simulator.configuration
     *          .ParameterDefinitionBuilder.ParameterDefinition
     * @see com.tacitknowledge.simulator.BaseConfigurable#parametersList
     */
    public List<List> getParametersList()
    {
        return getParametersDefinitionsAsList(parametersList);
    }
}
