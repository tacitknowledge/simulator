package com.tacitknowledge.simulator.camel;

import com.tacitknowledge.simulator.Adapter;
import com.tacitknowledge.simulator.Conversation;
import com.tacitknowledge.simulator.RouteManager;

import org.apache.camel.builder.RouteBuilder;
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
    private Map<String, RouteDefinition> convRoutes
            = new HashMap<String, RouteDefinition>();

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
        String conversationUniqueId = conversation.getUniqueId();

        RouteDefinition definition = convRoutes.get(conversationUniqueId);

        if (definition == null)
        {
            // --- Entry endpoint
            definition = this.from(conversation.getInboundTransport().toUriString());

            // --- Adapt input format, run scenarios and finally adapt into output format
            definition.bean(createAdapterWrapper(conversation.getInboundAdapter()));
            definition.bean(new ScenarioExecutionWrapper(conversation.getScenarios()));
            definition.bean(createAdapterWrapper(conversation.getOutboundAdapter()));

            // --- Exit endpoint
            definition.to(conversation.getOutboundTransport().toUriString());
            definition.setId(conversationUniqueId);
            convRoutes.put(conversationUniqueId, definition);

            logger.debug("Route : " + definition.getId() + " was added to the context : "
                    + getContext().getName());
        }
        else
        {
            getContext().startRoute(definition);
        }

    }

    /**
     * {@inheritDoc}
     */
    public void deactivate(Conversation conversation) throws Exception
    {
        RouteDefinition definition = convRoutes.get(conversation.getUniqueId());
        if (definition != null)
        {
            getContext().stopRoute(definition);

            logger.debug("Route : " + definition.getId() + " was stopped in the context : "
                    + getContext().getName());
        }
        else
        {
            logger.warn("Trying to deactivate route which is not active ");
        }
    }

    /**
     * Creates an AdapterWrapper object based on the provided adapter.
     *
     * @param adapter adapter to be wrapped.
     * @return AdapterWrapper object.
     */
    private AdapterWrapper createAdapterWrapper(Adapter adapter)
    {
        return new AdapterWrapper(adapter);
    }
}
