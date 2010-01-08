package com.tacitknowledge.simulator.configuration.beans;

import com.tacitknowledge.simulator.configuration.SimulatorEventType;
import com.tacitknowledge.simulator.configuration.EventDispatcher;
import com.tacitknowledge.simulator.Conversation;
import org.apache.log4j.Logger;

/**
 * Created by IntelliJ IDEA.
 * User: dvalencia
 * Date: Jan 7, 2010
 * Time: 5:35:01 PM
 * To change this template use File | Settings | File Templates.
 */
public class EventBean {
    private Conversation conversation;
    private SimulatorEventType eventType;

    /**
     * Logger for this class.
     */
    private static Logger logger = Logger.getLogger(EventBean.class);

    public EventBean(SimulatorEventType type, Conversation conv) {
        this.eventType = type;
        this.conversation = conv;
    }

    public String process(String body){
        try{
            EventDispatcher.getInstance().dispatchEvent(this.eventType, this.conversation, body);
        }catch(Exception ex){
            if(logger.isDebugEnabled()){
                logger.debug("Exception thrown dispatching event " + this.eventType + ". " + ex.getMessage());
            }
        }
        return body;
    }
}
