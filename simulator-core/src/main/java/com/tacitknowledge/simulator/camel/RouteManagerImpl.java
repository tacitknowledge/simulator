package com.tacitknowledge.simulator.camel;

import com.tacitknowledge.simulator.Conversation;
import com.tacitknowledge.simulator.RouteManager;
import com.tacitknowledge.simulator.SimulatorException;
import com.tacitknowledge.simulator.configuration.beans.EventBean;
import com.tacitknowledge.simulator.configuration.SimulatorEventType;

import org.apache.camel.CamelContext;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.jboss.JBossPackageScanClassResolver;
import org.apache.camel.model.RouteDefinition;
import org.apache.camel.spi.PackageScanClassResolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
    private static Logger logger = LoggerFactory.getLogger(RouteManagerImpl.class);

    /**
     * true if context has been started by camel
     */
    private boolean contextStarted = false;

    /**
     * Container for the routes inside the current camel context. Used for activation and
     * deactivation of the routes.
     */
    private Map<Integer, RouteDefinition> convRoutes = new HashMap<Integer, RouteDefinition>();


    /**
     * container for active routes ids.
     */
    private Set<Integer> activeRoutes = new HashSet<Integer>();


    /**
     * Default Constructor
     */
    public RouteManagerImpl()
    {
    	CamelContext context = new DefaultCamelContext();
    	try
    	{
    		//check if we run in JBoss. We could check for any class that is provided by Jboss.
    		Class.forName("org.jboss.logging.appender.RollingFileAppender");
        	PackageScanClassResolver jbossResolver = new JBossPackageScanClassResolver();
        	context.setPackageScanClassResolver(jbossResolver);
    		logger.info("Running in JBoss");
    	}
    	catch (ClassNotFoundException e)
    	{
    		//just ignore if we're not in jboss
    		logger.info("Not running in JBoss. Probably we're in Tomcat");
    	}

        setContext(context);
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
    public void activate(final Conversation conversation) throws Exception
    {
        Integer conversationId = conversation.getId();

        logger.info("Activating conversation: {}", conversation);

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
            // --- Adapt input format, run scenarios and finally adapt into output format
            definition.bean(new ScenarioExecutionWrapper(conversation));

            // --- Exit endpoint
            definition.to(outboundTransportURI);
            definition.bean(new EventBean(SimulatorEventType.RESPONSE_SENT, conversation));

            definition.setId(conversationId.toString());
            convRoutes.put(conversationId, definition);

            logger.info("Route : {} was added to the context : {}", definition.getId(),
                getContext().getName());

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
    public void deactivate(final Conversation conversation) throws Exception
    {
        logger.info("Deactivating conversation: {}", conversation);
        int i = conversation.getId();
        RouteDefinition definition = convRoutes.remove(i);
        if (definition != null)
        {
            getContext().stopRoute(definition);
            activeRoutes.remove(i);
            logger.info("Route : {} was stopped in the context : {}", definition.getId(),
                getContext().getName());
        }
        else
        {
            logger.warn("Trying to deactivate route which is not active ");
        }
    }

    /**
     * {@inheritDoc}
     */
    public boolean isActive(final Conversation conversation) throws SimulatorException
    {
        return activeRoutes.contains(conversation.getId());
    }

    /**
     * {@inheritDoc}
     */
    public void delete(final Conversation conversation) throws Exception
    {
        deactivate(conversation);
        convRoutes.remove(conversation.getId());
    }
}
