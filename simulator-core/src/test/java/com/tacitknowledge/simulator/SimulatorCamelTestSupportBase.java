package com.tacitknowledge.simulator;

import com.tacitknowledge.simulator.camel.RouteManagerImpl;
import com.tacitknowledge.simulator.transports.MockInTransport;
import com.tacitknowledge.simulator.transports.MockOutTransport;
import org.apache.camel.EndpointInject;
import org.apache.camel.Produce;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.junit4.CamelTestSupport;

/**
 * Simulator Camel Test Support Base
 *
 * @author Nikita Belenkiy (nbelenkiy@tacitknowledge.com)
 */
public class SimulatorCamelTestSupportBase extends CamelTestSupport
{
    /**
     * MockEnd point to receive the message
     */
    @EndpointInject(uri = "mock:result")
    protected MockEndpoint resultEndpoint;

    /**
     * MockEntry point to send the message
     */
    @Produce(uri = "direct:start")
    protected ProducerTemplate template;

    protected RouteManagerImpl routeManager = new RouteManagerImpl();

    protected Transport inTransport = new MockInTransport();
    protected Transport outTransport = new MockOutTransport();


    /**
     * Overriding the route builder as suggested by Camel testing
     * techniques.
     *
     * @return a route builder.
     * @throws Exception in case of an error.
     */
    @Override
    protected RouteBuilder createRouteBuilder() throws Exception
    {
        routeManager = new RouteManagerImpl();
        return routeManager;
    }

    /**
     * Utility method to send a message to Camel.
     *
     * @param expectedBody
     * @throws InterruptedException in case of an error
     */
    protected void sendMessage(String expectedBody) throws InterruptedException
    {
        String body = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?><matched/>";

        resultEndpoint.expectedBodiesReceived(expectedBody);

        template.sendBody(body);
    }
}
