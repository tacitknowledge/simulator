package com.tacitknowledge.simulator.configuration;

import com.tacitknowledge.simulator.Conversation;

import java.util.List;
import java.util.ArrayList;

import org.apache.log4j.Logger;

/**
 * EventDispatcher class
 *
 * @author Daniel Valencia (dvalencia@tacitknowledge.com)
 * @author Raul Huerta (rhuerta@tacitknowledge.com)
 */

public class EventDispatcher {

    /**
     * Because it's a singleton
     */
    private static EventDispatcher instance;

    /**
     * Logger for the EventDispatcherImpl class.
     */
    private static Logger logger
        = Logger.getLogger(EventDispatcher.class);

    /**
     * Default Constructor
     */
    private EventDispatcher() {
        this.eventListeners = new ArrayList<SimulatorEventListener>();
        this.eventListenerClassNames = new ArrayList<String>();
    }

    /**
     * Contains all registered event listeners
     */
    private List<SimulatorEventListener> eventListeners;

    /**
     * Contains all registered event listener class names
     */
    private List<String> eventListenerClassNames;

    /**
     * Singleton of this class
     * @return EventDispatcher
     */
    public static EventDispatcher getInstance(){
        if(instance == null){
           instance = new EventDispatcher();
        }
        return instance;
    }

    /**
     * Add an event listener to a dispatcher
     * @param listener - SimulatorEventListener implementation to be added
     */
    public void addSimulatorEventListener(SimulatorEventListener listener) {
        String className = listener.getClass().getName();
        if(!this.eventListenerClassNames.contains(className)) {
            this.eventListeners.add(listener);
            this.eventListenerClassNames.add(className);
        }
    }

    /**
     * Returns all SimulatorEventListener objects added
     * @return - List of SimulatorEventListener objects
     */
    public List<SimulatorEventListener> getSimulatorEventListeners() {
        return this.eventListeners;
    }

    /**
     * Clean up all event listeners
     */
    public void removeAllSimulatorEventListeners() {
        this.eventListeners = new ArrayList<SimulatorEventListener>();
        this.eventListenerClassNames = new ArrayList<String>();
    }

    /**
     * Dispatches the event specified by eventType
     * @param eventType the type of event to dispatch
     * @param conversation the conversation related to this event
     * @param messageBody a string containing the message body
     */
    public void dispatchEvent(SimulatorEventType eventType, Conversation conversation, String messageBody) {
        for(SimulatorEventListener listener : this.eventListeners){
            try{
                switch(eventType){
                    case NEW_MESSAGE:
                        listener.onNewMessage(messageBody, conversation);
                        break;
                    case RESPONSE_BUILT:
                        listener.onResponseBuilt(messageBody, conversation);
                        break;
                    case RESPONSE_SENT:
                        listener.onResponseSent(messageBody, conversation);
                        break;
                    case SCENARIO_MATCHED:
                        listener.onMatchingScenario(messageBody, conversation);
                        break;
                    default:
                        break;
                }
            }catch (Exception ex){
                if(logger.isDebugEnabled()){
                    logger.debug("Exception thrown executing listener " + listener.getClass() + ". " + ex.getMessage());
                }
            }
        }
    }
}
