package com.tacitknowledge.simulator.transports;

import com.tacitknowledge.simulator.Transport;

/**
 * Transport implementation for Jms endpoints.
 *
 * @author galo
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
     * Flag for determining if JSM transport type, Apache ActiveMQ or generic JMS. Defaults to generic JMS
     */
    private boolean activeMQ;

    /**
     * Constructor
     *
     * @param destinationName
     */
    public JmsTransport(String destinationName)
    {
        this.destinationName = destinationName;
    }

    /**
     * Constructor
     *
     * @param topicName
     * @param destinationName
     * @param activeMQ
     */
    public JmsTransport(String topicName, String destinationName, boolean activeMQ)
    {
        this.topicName = topicName;
        this.destinationName = destinationName;
        this.activeMQ = activeMQ;
    }

    /**
     * @inheritDoc
     */
    public String toUriString()
    {
        StringBuilder sb = new StringBuilder();

        sb.append(isActiveMQ() ? "activemq:" : "jms:");

        if (topicName != null)
        {
            sb.append(topicName).append(":");
        }

        sb.append(destinationName);

        return sb.toString();
    }

    /**
     * @return
     * @see #topicName
     */
    public String getTopicName()
    {
        return topicName;
    }

    /**
     * @param topicName
     * @see #topicName
     */
    public void setTopicName(String topicName)
    {
        this.topicName = topicName;
    }

    /**
     * @return
     * @see #destinationName
     */
    public String getDestinationName()
    {
        return destinationName;
    }

    /**
     * @param destinationName
     * @see #destinationName
     */
    public void setDestinationName(String destinationName)
    {
        this.destinationName = destinationName;
    }

    /**
     * @return
     * @see #activeMQ
     */
    public boolean isActiveMQ()
    {
        return activeMQ;
    }

    /**
     * @param activeMQ
     * @see #activeMQ
     */
    public void setActiveMQ(boolean activeMQ)
    {
        this.activeMQ = activeMQ;
    }
}