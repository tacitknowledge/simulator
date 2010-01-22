package com.tacitknowledge.simulator.configuration;

import com.tacitknowledge.simulator.Conversation;
import org.apache.camel.Exchange;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * EventDispatcher class
 *
 * @author Daniel Valencia (dvalencia@tacitknowledge.com)
 * @author Raul Huerta (rhuerta@tacitknowledge.com)
 */

public final class EventDispatcher
{

    /**
     * Because it's a singleton
     */
    private static EventDispatcher instance;

    /**
     * Logger for the EventDispatcherImpl class.
     */
    private static Logger logger = LoggerFactory.getLogger(EventDispatcher.class);

    /**
     * Contains all registered event listeners
     */
    private List<SimulatorEventListener> eventListeners;

    /**
     * Contains all registered event listener class names
     */
    private List<String> eventListenerClassNames;

    /**
     * Default Constructor
     */
    private EventDispatcher()
    {
        this.eventListeners = new ArrayList<SimulatorEventListener>();
        this.eventListenerClassNames = new ArrayList<String>();
    }


    /**
     * Singleton of this class
     *
     * @return EventDispatcher
     */
    public static EventDispatcher getInstance()
    {
        if (instance == null)
        {
            instance = new EventDispatcher();
        }
        return instance;
    }

    /**
     * Add an event listener to a dispatcher
     *
     * @param listener - SimulatorEventListener implementation to be added
     */
    public void addSimulatorEventListener(final SimulatorEventListener listener)
    {
        String className = listener.getClass().getName();
        if (!this.eventListenerClassNames.contains(className))
        {
            this.eventListeners.add(listener);
            this.eventListenerClassNames.add(className);
        }
    }

    /**
     * Returns all SimulatorEventListener objects added
     *
     * @return - List of SimulatorEventListener objects
     */
    public List<SimulatorEventListener> getSimulatorEventListeners()
    {
        return this.eventListeners;
    }

    /**
     * Clean up all event listeners
     */
    public void removeAllSimulatorEventListeners()
    {
        this.eventListeners = new ArrayList<SimulatorEventListener>();
        this.eventListenerClassNames = new ArrayList<String>();
    }

    /**
     * Dispatches the event specified by eventType
     *
     * @param eventType    the type of event to dispatch
     * @param conversation the conversation related to this event
     * @param exchange     a string containing the message body
     */
    public void dispatchEvent(final SimulatorEventType eventType,
                              final Conversation conversation, final Exchange exchange)
    {
        for (SimulatorEventListener listener : this.eventListeners)
        {
            try
            {
                switch (eventType)
                {
                    case NEW_MESSAGE:
                        listener.onNewMessage(exchange, conversation);
                        break;
                    case RESPONSE_BUILT:
                        listener.onResponseBuilt(exchange, conversation);
                        break;
                    case RESPONSE_SENT:
                        listener.onResponseSent(exchange, conversation);
                        break;
                    case SCENARIO_MATCHED:
                        listener.onMatchingScenario(exchange, conversation);
                        break;
                    default:
                        break;
                }
            }
            catch (Exception ex)
            {
                logger.error("Exception thrown executing listener " + listener.getClass() , ex);
            }
        }
    }
}
