package com.tacitknowledge.simulator.camel;


import com.tacitknowledge.simulator.Adapter;
import com.tacitknowledge.simulator.Conversation;
import org.apache.camel.CamelContext;
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
public class RouteManager extends RouteBuilder
{
    /**
     * Logger for this class.
     */
    private static Logger logger = Logger.getLogger(RouteManager.class);

    /**
     * Container for the routes inside the current camel context.
     * Used for activation and deactivation of the routes.
     */
    private Map<Conversation, RouteDefinition> convRoutes = new HashMap<Conversation, RouteDefinition>();

    /**
     * Constructor for the RouteManager.
     * Will create a new camel context and starts it.
     *
     * @throws Exception in case of an error.
     */
    public RouteManager() throws Exception
    {
        CamelContext camelContext = new DefaultCamelContext();
        setContext(camelContext);
        camelContext.start();
    }


    public void configure() throws Exception
    {

    }

    /**
     * Builds simulation route using conversation object.
     * Assigns adapter beans to the route, assigns simulation execution bean to the route.
     * Adds route to the current camel context.
     *
     * @param conversation object to be used in the route.
     * @throws Exception in case of an error
     */
    public void activate(Conversation conversation) throws Exception
    {
        RouteDefinition definition = convRoutes.get(conversation);

        if (definition == null)
        {
            definition = this.from(conversation.getInboundTransport().toUriString());

            definition.bean(createAdapterWrapper(conversation.getInboundAdapter()));
            definition.bean(new ScenarioExecutionWrapper(conversation.getScenarios()));
            definition.bean(createAdapterWrapper(conversation.getOutboundAdapter()));
            
            getContext().addRoutes(this);
            
            logger.debug("Route : " + definition.getId() + " was added to the context : " + getContext().getName());
            
            convRoutes.put(conversation, definition);
        }
        else
        {
            getContext().startRoute(definition);
        }

    }

    /**
     * Stops the camel route without removing it from the context.
     *
     * @param conversation object to be used in the route.
     * @throws Exception in case of an error.
     */
    public void deactivate(Conversation conversation) throws Exception
    {
        RouteDefinition definition = convRoutes.get(conversation);
        getContext().stopRoute(definition);
        
        logger.debug("Route : " + definition.getId() + " was stopped in the context : " + getContext().getName());
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
