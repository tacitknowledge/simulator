package com.tacitknowledge.simulator.configuration.beans;

import com.tacitknowledge.simulator.configuration.SimulatorEventType;
import com.tacitknowledge.simulator.configuration.EventDispatcher;
import com.tacitknowledge.simulator.Conversation;
import org.apache.log4j.Logger;
import org.apache.camel.Exchange;

/**
 * This class will be used as an endpoint in the camel route, to dispatch different events.
 *
 * @author Daniel Valencia (mailto:dvalencia@tacitknowledge.com)
 */
public class EventBean {
    private Conversation conversation;
    private SimulatorEventType eventType;

    /**
     * Logger for this class.
     */
    private static Logger logger = Logger.getLogger(EventBean.class);

    /**
     * Default Constructor.
     * @param type the event type.
     * @param conv the conversation related to this Event.
     */
    public EventBean(SimulatorEventType type, Conversation conv) {
        this.eventType = type;
        this.conversation = conv;
    }

    /**
     * Method to dispatch an event.
     * When this class is used as an endpoint, camel will call this method automatically.
     * @param exchange the message body.
     * @return the message body.
     */
    public void process(Exchange exchange){
        try{
            EventDispatcher.getInstance().dispatchEvent(this.eventType, this.conversation, exchange);
            logger.info("Event dispatched: " + this.eventType);
        }catch(Exception ex){
            if(logger.isDebugEnabled()){
                logger.debug("Exception thrown dispatching event " + this.eventType + ". " + ex.getMessage());
            }
        }
    }
}
