package com.tacitknowledge.simulator.transports;

import com.tacitknowledge.simulator.Transport;

/**
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
     * Flag for determining if this transport is an Apache ActiveMQ implementation
     */
    private boolean activeMQ;

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

    /**
     * @inheritDoc
     */
    public String toUriString()
    {
        return null;
    }
}