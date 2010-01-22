package com.tacitknowledge.simulator.configuration.beans;

import com.tacitknowledge.simulator.Conversation;
import com.tacitknowledge.simulator.configuration.EventDispatcher;
import com.tacitknowledge.simulator.configuration.SimulatorEventType;
import org.apache.camel.Exchange;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class will be used as an endpoint in the camel route, to dispatch different events.
 *
 * @author Daniel Valencia (mailto:dvalencia@tacitknowledge.com)
 */
public class EventBean
{

    /**
     * Logger for this class.
     */
    private static Logger logger = LoggerFactory.getLogger(EventBean.class);

    /**
     * Conversation object
     */
    private Conversation conversation;

    /**
     * SimulatorEventType object
     */
    private SimulatorEventType eventType;

    /**
     * Default Constructor.
     *
     * @param type the event type.
     * @param conv the conversation related to this Event.
     */
    public EventBean(final SimulatorEventType type, final Conversation conv)
    {
        this.eventType = type;
        this.conversation = conv;
    }

    /**
     * Method to dispatch an event.
     * When this class is used as an endpoint, camel will call this method automatically.
     *
     * @param exchange the message body.
     */
    public void process(final Exchange exchange)
    {
        try
        {
            EventDispatcher.getInstance().dispatchEvent(this.eventType, this.conversation,
                                                                                    exchange);
            logger.info("Event dispatched: {}", this.eventType);
        }
        catch (Exception ex)
        {
            logger.error("Exception thrown dispatching event " + this.eventType + ". ", ex);
        }
    }
}
