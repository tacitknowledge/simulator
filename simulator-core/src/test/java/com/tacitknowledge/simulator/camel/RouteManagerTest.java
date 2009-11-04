package com.tacitknowledge.simulator.camel;

import com.tacitknowledge.simulator.Conversation;
import com.tacitknowledge.simulator.formats.JsonAdapter;
import com.tacitknowledge.simulator.transports.Transport;
import org.apache.camel.EndpointInject;
import org.apache.camel.Produce;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.junit.Test;

/**
 * @author nikitabelenkiy
 */
public class RouteManagerTest extends CamelTestSupport
{
    RouteManager routeManager;

    @EndpointInject(uri = "mock:result")
    protected MockEndpoint resultEndpoint;

    @Produce(uri = "direct:start")
    protected ProducerTemplate template;

    final private Transport inTransport = new Transport()
    {

        public String getType()
        {
            return "file";
        }

        public String toUriString()
        {
            return "direct:start";
        }
    };

    final private Transport outTransport = new Transport()
    {

        public String getType()
        {
            return "file";
        }

        public String toUriString()
        {
            return "mock:result";
        }
    };


    @Test
    public void testActivate() throws Exception
    {


        String expectedBody = "<matched/>";

        resultEndpoint.expectedBodiesReceived(expectedBody);

        template.sendBody(expectedBody);

        resultEndpoint.assertIsSatisfied();

    }


    @Override
    protected RouteBuilder createRouteBuilder() throws Exception
    {
        final Conversation conversation = new Conversation(1, inTransport, outTransport, new JsonAdapter(), new JsonAdapter());

        routeManager = new RouteManager();
        routeManager.activate(conversation);
        return routeManager;
    }

//    @Test
//    public void testDeactivate()
//    {
//        // Add your code here
//    }
}
