package com.tacitknowledge.simulator.camel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.camel.CamelContext;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.model.RouteDefinition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tacitknowledge.simulator.Conversation;
import com.tacitknowledge.simulator.RouteManager;
import com.tacitknowledge.simulator.SimulatorException;

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
    private static Logger logger = LoggerFactory.getLogger(RouteManagerImpl.class);

    /**
     * true if context has been started by camel
     */
    private boolean contextStarted = false;

    /**
     * Container for the routes inside the current camel context. Used for activation and
     * deactivation of the routes.
     */
    private Map<String, RouteDefinition> routes = new HashMap<String, RouteDefinition>();

    /**
     * container for active routes ids.
     */
    private Set<String> activeRoutes = new HashSet<String>();

    /**
     * Default Constructor
     */
    public RouteManagerImpl()
    {
        DefaultCamelContext defaultCamelContext = new DefaultCamelContext();
        defaultCamelContext.setPackageScanClassResolver(new OneJarPackageScanResolver());
        setContext(defaultCamelContext);
    }

    /**
     * {@inheritDoc}
     */
    public void configure() throws Exception
    {}
    
    /**
     * {@inheritDoc}
     */
    public void start() throws Exception
    {
        if (!contextStarted)
        {
            getContext().start();
            contextStarted = true;
        }
    }

    /**
     * {@inheritDoc}
     */
    public void activate(final Conversation conversation) throws Exception
    {
        ensureThatContextIsStarted();
        
        CamelContext context = getContext();
        String conversationId = conversation.getId();
        RouteDefinition definition = routes.get(conversationId);

        logger.info("Activating conversation: {}", conversation);

        if (definition == null)
        {
            String inboundTransportURI = conversation.getInboundTransport().toUriString();
            String outboundTransportURI = conversation.getOutboundTransport().toUriString();

            definition = from(inboundTransportURI);
            definition.bean(conversation);
            definition.to(outboundTransportURI);
            definition.setId(conversationId);

            routes.put(conversationId, definition);

            logger.info("Route : {} was added to the context : {}", 
                        definition.getId(),
                        context.getName());

            context.startRoute(definition);
            activeRoutes.add(conversationId);
        }
        else
        {
            if (!activeRoutes.contains(conversationId))
            {
                activeRoutes.add(conversationId);
                context.startRoute(definition);
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    public void deactivate(final Conversation conversation) throws Exception
    {
        ensureThatContextIsStarted();
        
        String id = conversation.getId();
        stopRoute(id);
        removeRoute(id);
    }

    private void stopRoute(String id) throws Exception
    {
        ensureThatContextIsStarted();
        
        CamelContext context = getContext();
        RouteDefinition definition = routes.get(id);

        if (definition != null)
        {
            context.stopRoute(definition);
            logger.info("Route : {} was stopped in the context : {}",
                        definition.getId(),
                        context.getName());
        }
        else
        {
            logger.warn("Trying to deactivate route which is not active ");
        }
    }

    private void removeRoute(String id)
    {
        routes.remove(id);
        activeRoutes.remove(id);
    }

    /**
     * {@inheritDoc}
     */
    public boolean isActive(final Conversation conversation) throws SimulatorException
    {
        ensureThatContextIsStarted();
        
        return activeRoutes.contains(conversation.getId());
    }

    /**
     * {@inheritDoc}
     */
    public void delete(final Conversation conversation) throws Exception
    {
        ensureThatContextIsStarted();
        
        deactivate(conversation);
        routes.remove(conversation.getId());
    }

    /**
     * {@inheritDoc}
     */
    public void stop() throws Exception
    {
        if (contextStarted)
        {
            stopAllActiveRoutes();
            getContext().stop();
            contextStarted = false;
        }
    }

    private void stopAllActiveRoutes()
    {
        List<String> stoppedRoutes = new ArrayList<String>();

        try
        {
            for (String id : activeRoutes)
            {
                stopRoute(id);
                stoppedRoutes.add(id);
            }
        }
        catch (Exception e)
        {
            logger.error(e.getMessage());
        }
        finally
        {
            for (String id : stoppedRoutes)
            {
                removeRoute(id);
            }
        }
    }

    private void ensureThatContextIsStarted()
    {
        if(!contextStarted)
        {
            throw new IllegalStateException("Please start the route manager by calling the start() method"); 
        }
    }
}
