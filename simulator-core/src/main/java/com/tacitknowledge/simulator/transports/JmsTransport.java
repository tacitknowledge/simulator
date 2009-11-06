package com.tacitknowledge.simulator.transports;

import com.tacitknowledge.simulator.Transport;

/**
 * Transport implementation for Jms endpoints.
 *
 * @author Jorge Galindo (jgalindo@tacitknowledge.com)
 */
public class JmsTransport extends BaseTransport implements Transport
{
    /**
     * JMS topic name (optional)
     */
    private String topicName;

    /**
     * JMS Destination name
     */
    private String destinationName;

    /**
     * Flag for determining if JSM transport type, Apache ActiveMQ or generic JMS. Defaults to
     * generic JMS
     */
    private boolean activeMQ;

    /**
     * Constructor for the JmsTransport class.
     *
     * @param destinationName @see #destinationName
     */
    public JmsTransport(String destinationName)
    {
        this.destinationName = destinationName;
    }

    /**
     * Constructor for the JmsTransport class, has an option for ActiveMQ
     *
     * @param topicName @see #topicName
     * @param destinationName @see #destinationName
     * @param activeMQ @see #activeMQ
     */
    public JmsTransport(String topicName, String destinationName, boolean activeMQ)
    {
        this.topicName = topicName;
        this.destinationName = destinationName;
        this.activeMQ = activeMQ;
    }

    /**
     * @inheritDoc
     * @return @see #Transport.toUriString()
     */
    public String toUriString()
    {
        StringBuilder sb = new StringBuilder();

        if (isActiveMQ())
        {
            sb.append("activemq:");
        }
        else
        {
            sb.append("jms:");
        }

        if (topicName != null)
        {
            sb.append(topicName).append(":");
        }

        sb.append(destinationName);

        return sb.toString();
    }

    /**
     * Getter for @see #topicName
     * @return @see #topicName
     */
    public String getTopicName()
    {
        return topicName;
    }

    /**
     * Setter for @see #topicName
     * @param topicName @see #topicName
     */
    public void setTopicName(String topicName)
    {
        this.topicName = topicName;
    }

    /**
     * Getter for @see #destinationName
     * @return @see #destinationName
     */
    public String getDestinationName()
    {
        return destinationName;
    }

    /**
     * Setter for @see #destinationName
     * @param destinationName @see #destinationName
     */
    public void setDestinationName(String destinationName)
    {
        this.destinationName = destinationName;
    }

    /**
     * Getter for @see #activeMQ
     * @return @see #activeMQ
     */
    public boolean isActiveMQ()
    {
        return activeMQ;
    }

    /**
     * Setter for @see #activeMQ
     * @param activeMQ @see #activeMQ
     */
    public void setActiveMQ(boolean activeMQ)
    {
        this.activeMQ = activeMQ;
    }
}
