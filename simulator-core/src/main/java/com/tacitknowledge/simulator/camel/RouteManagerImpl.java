package com.tacitknowledge.simulator.camel;

import com.tacitknowledge.simulator.Conversation;
import com.tacitknowledge.simulator.RouteManager;
import com.tacitknowledge.simulator.SimulatorException;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.model.RouteDefinition;
import org.apache.log4j.Logger;

import java.util.HashMap;
import java.util.Map;

/**
 * Manages Camel routes based on the provided conversation objects.
 *
 * @author Jorge Galindo (jgalindo@tacitknowledge.com)
 * @author Nikita Belenkiy (nbelenkiy@tacitknowledge.com)
 */
public class RouteManagerImpl extends RouteBuilder implements RouteManager {
    /**
     * Logger for this class.
     */
    private static Logger logger = Logger.getLogger(RouteManagerImpl.class);

    /**
     * Container for the routes inside the current camel context. Used for activation and
     * deactivation of the routes.
     */
    private Map<Integer, RouteDefinition> convRoutes = new HashMap<Integer, RouteDefinition>();
    boolean contextStarted = false;

    public RouteManagerImpl() {
       super(new DefaultCamelContext());
    }

    /**
     * {@inheritDoc}
     */
    public void configure() throws Exception {

    }

    /**
     * {@inheritDoc}
     */
    public void activate(Conversation conversation) throws Exception {
        Integer conversationId = conversation.getId();

        RouteDefinition definition = convRoutes.get(conversationId);

        if (!contextStarted) {
            getContext().start();
            contextStarted = true;
        }
        if (definition == null) {
            // --- Entry endpoint
            definition = this.from(conversation.getInboundTransport().toUriString());

            // --- Adapt input format, run scenarios and finally adapt into output format
            definition.bean(new ScenarioExecutionWrapper(conversation.getScenarios(), conversation
                    .getInboundAdapter(), conversation.getOutboundAdapter()));

            // --- Exit endpoint
            definition.to(conversation.getOutboundTransport().toUriString());
            definition.setId(conversationId.toString());
            convRoutes.put(conversationId, definition);

            logger.debug("Route : " + definition.getId() + " was added to the context : "
                    + getContext().getName());

            getContext().startRoute(definition);
        } else {
            getContext().startRoute(definition);
        }
    }

    /**
     * {@inheritDoc}
     */
    public void deactivate(Conversation conversation) throws Exception {
        RouteDefinition definition = convRoutes.get(conversation.getId());
        if (definition != null) {
            getContext().stopRoute(definition);
            logger.debug("Route : " + definition.getId() + " was stopped in the context : "
                    + getContext().getName());
        } else {
            logger.warn("Trying to deactivate route which is not active ");
        }
    }

    /**
     * {@inheritDoc}
     */
    public boolean isActive(Conversation conversation) throws SimulatorException {
        return convRoutes.get(conversation.getId()) != null;
    }
}
