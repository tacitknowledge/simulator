package com.tacitknowledge.simulator.transports;

import com.tacitknowledge.simulator.Conversation;
import com.tacitknowledge.simulator.ConversationManager;
import com.tacitknowledge.simulator.ConversationNotFoundException;
import com.tacitknowledge.simulator.SimulatorException;
import com.tacitknowledge.simulator.camel.RouteManagerImpl;
import com.tacitknowledge.simulator.formats.XmlAdapter;
import com.tacitknowledge.simulator.impl.ConversationManagerImpl;
import org.apache.camel.EndpointInject;
import org.apache.camel.Exchange;
import org.apache.camel.Produce;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.apache.ftpserver.FtpServer;
import org.apache.ftpserver.FtpServerFactory;
import org.apache.ftpserver.ftplet.FtpException;
import org.apache.ftpserver.listener.ListenerFactory;
import org.apache.ftpserver.usermanager.Md5PasswordEncryptor;
import org.apache.ftpserver.usermanager.impl.PropertiesUserManager;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Test class for FtpTransportIntegration
 *
 * @author Nikita Belenkiy (nbelenkiy@tacitknowledge.com)
 */
public class FtpTransportIntegrationTest extends CamelTestSupport
{
    private FtpServer server;
    private FtpTransport ftpTransport;
    private MockOutTransport out = new MockOutTransport();

    /**
     * MockEnd point to receive the message
     */
    @EndpointInject(uri = "mock:result")
    private MockEndpoint resultEndpoint;

    /**
     * MockEntry point to send the message
     */
    @Produce(uri = "direct:start")
    private ProducerTemplate template;

    private RouteManagerImpl routeManager;


    @Before
    public void setUp() throws Exception
    {
        super.setUp();
        new File("./src/test/resources/ftp/admin").mkdirs();
        new File("./src/test/resources/ftp/anonymous").mkdirs();
        FtpServerFactory serverFactory = new FtpServerFactory();
        serverFactory.setUserManager(new PropertiesUserManager(new Md5PasswordEncryptor(), new File("user.properties"), "admin"));
        ListenerFactory factory = new ListenerFactory();
        factory.setPort(2222);
        serverFactory.addListener("default", factory.createListener());
        server = serverFactory.createServer();

        // start the server
        server.start();

        ftpTransport = new FtpTransport();
        Map<String, String> params = new HashMap<String, String>();
        params.put(FtpTransport.PARAM_HOST, "localhost");
        params.put(FtpTransport.PARAM_PORT, "2222");
        params.put(FtpTransport.PARAM_USERNAME, "admin");
        params.put(FtpTransport.PARAM_PASSWORD, "admin");
        params.put(FtpTransport.PARAM_FILE_NAME, "testxml.xml");
        ftpTransport.setParameters(params);
    }

    @After
    public void tearDown() throws Exception
    {
        super.tearDown();
        server.stop();
    }

    @Test
    public void testSimple() throws FtpException, SimulatorException, InterruptedException, ConversationNotFoundException
    {
        ConversationManager manager = new ConversationManagerImpl(routeManager);

        Conversation conversation = manager.createOrUpdateConversation(1, "testSimple", ftpTransport, out, new XmlAdapter(), new XmlAdapter(), "");
        conversation.addOrUpdateScenario(1, "javascript", "true", "employees.employee[0].name='John12345';employees");
        Assert.assertNotNull(conversation);
        manager.activate(1);
        Thread.sleep(10000);
        List<Exchange> list = resultEndpoint.getReceivedExchanges();
        assertTrue(list.get(0).getIn().getBody().toString().contains("John12345"));
    }

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

}
