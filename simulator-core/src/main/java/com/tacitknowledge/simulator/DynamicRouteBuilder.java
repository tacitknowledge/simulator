package com.tacitknowledge.simulator;


import org.apache.camel.CamelContext;
import org.apache.camel.Route;
import org.apache.camel.model.RouteDefinition;
import org.apache.camel.builder.RouteBuilder;

import java.util.List;

/**
 *
 *
 * @author galo
 */
public class DynamicRouteBuilder extends RouteBuilder {
    /**
     *
     * @param context The CamelContext to which the newly created routes will be added to.
     */
    public DynamicRouteBuilder(CamelContext context) {
        super(context);
    }

    public void configure() throws Exception {
    }

    /**
     * Generates and registers a new Camel route to the current CamelContext.
     * @param from The route's starting Endpoint string representation.<br/>
     *  Would usually be a incoming message/event from an external source (file sys, tcp, http, jms, etc)
     * @param tos A list of endpoints in their string-representations that would process the first message received as a pipeline
     */
    public void createNewCamelRoute(String from, List<String> tos) {
        RouteDefinition route = this.from(from);

        for (String to : tos) {
            route.to(to);
        }
        System.out.println("*** Adding new route to Camel context: " + route.toString());

        try {
            getContext().addRoutes(this);
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Generates and registers a new Camel Route to the current CamelContext based in the provided Conversation
     * @param conversation The Conversarion to be registered as a CamelRoute
     */
    public void buildRoute(Conversation conversation) {
        
    }
}
