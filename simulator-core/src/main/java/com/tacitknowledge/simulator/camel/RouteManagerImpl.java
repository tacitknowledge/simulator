package com.tacitknowledge.simulator.camel;

import com.tacitknowledge.simulator.Conversation;
import com.tacitknowledge.simulator.RouteManager;
import com.tacitknowledge.simulator.SimulatorException;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.model.RouteDefinition;
import org.apache.log4j.Logger;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Manages Camel routes based on the provided conversation objects.
 *
 * @author Jorge Galindo (jgalindo@tacitknowledge.com)
 * @author Nikita Belenkiy (nbelenkiy@tacitknowledge.com)
 */
public class RouteManagerImpl extends RouteBuilder implements RouteManager
{
    /**
     * Logger for this class.
     */
    private static Logger logger = Logger.getLogger(RouteManagerImpl.class);

    /**
     * Container for the routes inside the current camel context. Used for activation and
     * deactivation of the routes.
     */
    private Map<Integer, RouteDefinition> convRoutes = new HashMap<Integer, RouteDefinition>();
    /**
     * container for active routes ids.
     */
    Set<Integer> activeRoutes = new HashSet<Integer>();

    boolean contextStarted = false;

    public RouteManagerImpl()
    {
        super(new DefaultCamelContext());
    }

    /**
     * {@inheritDoc}
     */
    public void configure() throws Exception
    {

    }

    /**
     * {@inheritDoc}
     */
    public void activate(Conversation conversation) throws Exception
    {
        Integer conversationId = conversation.getId();

        logger.info("Activating conversation: " + conversation);

        RouteDefinition definition = convRoutes.get(conversationId);

        if (!contextStarted)
        {
            getContext().start();
            contextStarted = true;
        }
        if (definition == null)
        {

            // --- Entry endpoint
            String inboundTransportURI = conversation.getInboundTransport().toUriString();
            String outboundTransportURI = conversation.getOutboundTransport().toUriString();

            definition = this.from(inboundTransportURI);
            definition.bean(new LoggingBean(true, conversation));
            // --- Adapt input format, run scenarios and finally adapt into output format
            definition.bean(new ScenarioExecutionWrapper(conversation.getScenarios(), conversation
                .getInboundAdapter(), conversation.getOutboundAdapter()));
            definition.bean(new LoggingBean(false, conversation));

            // --- Exit endpoint
            definition.to(outboundTransportURI);
            definition.setId(conversationId.toString());
            convRoutes.put(conversationId, definition);

            logger.info("Route : " + definition.getId() + " was added to the context : "
                + getContext().getName());

            getContext().startRoute(definition);
            activeRoutes.add(conversationId);

        }
        else
        {
            if (!activeRoutes.contains(conversationId))
            {
                activeRoutes.add(conversationId);
                getContext().startRoute(definition);
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    public void deactivate(Conversation conversation) throws Exception
    {
        logger.info("Deactivating conversation: " + conversation);
        int i = conversation.getId();
        RouteDefinition definition = convRoutes.get(i);
        if (definition != null)
        {
            getContext().stopRoute(definition);
            activeRoutes.remove(i);
            logger.info("Route : " + definition.getId() + " was stopped in the context : "
                + getContext().getName());
        }
        else
        {
            logger.warn("Trying to deactivate route which is not active ");
        }
    }

    /**
     * {@inheritDoc}
     */
    public boolean isActive(Conversation conversation) throws SimulatorException
    {
        return activeRoutes.contains(conversation.getId());
    }

    /**
     * {@inheritDoc}
     */
    public void delete(Conversation conversation) throws Exception
    {
        deactivate(conversation);
        convRoutes.remove(conversation.getId());
    }
}
