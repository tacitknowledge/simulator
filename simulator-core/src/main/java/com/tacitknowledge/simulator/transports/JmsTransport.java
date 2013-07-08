package com.tacitknowledge.simulator.transports;

import java.util.HashMap;
import java.util.Map;

import com.tacitknowledge.simulator.*;
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
        this(new BaseConfigurable(Configurable.BOUND_IN,new HashMap<String, String>()));
    }


    /**
     * @param configurable in or out
     * @inheritDoc
     */
    public JmsTransport(final Configurable configurable)
    {
        super(TransportConstants.JMS, configurable);
    }


    /**
     * {@inheritDoc}
     */
    protected String getUriString() throws ConfigurableException, TransportException
    {
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

        sb.append(configurable.getParamValue(PARAM_DESTINATION_NAME));

        sb.append("?brokerURL=").append(configurable.getParamValue(PARAM_BROKER_URL));

        if (configurable.getParamValue(PARAM_USER_NAME) != null)
        {
            sb.append(AMP).append("username=").append(configurable.getParamValue(PARAM_USER_NAME));
        }
        if (configurable.getParamValue(PARAM_PASSWORD) != null)
        {
            sb.append(AMP).append("password=").append(configurable.getParamValue(PARAM_PASSWORD));
        }

        logger.info("Uri String: {}", sb.toString());

        return sb.toString();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void validateParameters() throws ConfigurableException
    {
        if (configurable.getParamValue(PARAM_ACTIVE_MQ) != null)
        {
            this.activeMQ = Boolean.parseBoolean(configurable.getParamValue(PARAM_ACTIVE_MQ));
        }
        if (configurable.getParamValue(PARAM_IS_TOPIC) != null)
        {
            this.isTopic = Boolean.parseBoolean(configurable.getParamValue(PARAM_IS_TOPIC));
        }

        if (configurable.getParamValue(PARAM_DESTINATION_NAME) == null)
        {
            throw new ConfigurableException("Destination name parameter is required");
        }
        if (configurable.getParamValue(PARAM_BROKER_URL) == null)
        {
            throw new ConfigurableException("Broker URL is required.");
        }
    }
}
