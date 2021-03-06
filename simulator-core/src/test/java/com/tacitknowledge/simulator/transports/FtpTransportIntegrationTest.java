package com.tacitknowledge.simulator.transports;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import com.tacitknowledge.simulator.*;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.apache.ftpserver.FtpServer;
import org.apache.ftpserver.FtpServerFactory;
import org.apache.ftpserver.listener.ListenerFactory;
import org.apache.ftpserver.usermanager.Md5PasswordEncryptor;
import org.apache.ftpserver.usermanager.impl.PropertiesUserManager;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.tacitknowledge.simulator.camel.RouteManagerImpl;
import com.tacitknowledge.simulator.formats.XmlAdapter;
import com.tacitknowledge.simulator.impl.ConversationFactory;
import com.tacitknowledge.simulator.impl.ScenarioFactory;

/**
 * Test class for FtpTransportIntegration
 *
 * @author Nikita Belenkiy (nbelenkiy@tacitknowledge.com)
 */
public class FtpTransportIntegrationTest extends CamelTestSupport
{
    private FtpServer ftpServer;
    private Transport ftpInTransport;
    private Transport fileOutTransport;
    private RouteManagerImpl routeManager;

    @Before
    public void setUp() throws Exception
    {
        super.setUp();
        
        routeManager = new RouteManagerImpl();
        routeManager.start();
        
        ftpServer = createFtpServer();        
        ftpServer.start();
        
        Map<String, String> parameters = new HashMap<String, String>();
        parameters.put(FtpTransport.PARAM_HOST, "localhost");
        parameters.put(FtpTransport.PARAM_PORT, "2222");
        parameters.put(FtpTransport.PARAM_USERNAME, "admin");
        parameters.put(FtpTransport.PARAM_PASSWORD, "admin");
        parameters.put(FtpTransport.PARAM_FILE_NAME, "input.xml");
        
        ftpInTransport = createFtpTransport(parameters);
        
        parameters = new HashMap<String, String>();
        parameters.put(FileTransport.PARAM_DIRECTORY_NAME, getCurrentPathName() + "/ftp/admin");
        parameters.put(FileTransport.PARAM_FILE_NAME, "output.xml");
        fileOutTransport = createFileTransport(parameters);
    }

    @After
    public void tearDown() throws Exception
    {
        super.tearDown();
        ftpServer.stop();        
    }

    @Test
    public void testSimple() throws Exception
    {
        ScenarioFactory scenarioFactory = new ScenarioFactory();
        ConversationFactory conversationFactory = new ConversationFactory();
        Conversation conversation = conversationFactory.createConversation("somepath", 
                                                         ftpInTransport, 
                                                         fileOutTransport, 
                                                         new XmlAdapter(), 
                                                         new XmlAdapter());

        Scenario scenario = scenarioFactory.createScenario("file.scn", "javascript", "true", "employees.employee[0].name='Mike';employees");
        conversation.addScenario(scenario);
        
        routeManager.activate(conversation);
        
        Thread.sleep(10000);
        
        File file = new File(getCurrentPathName() + "/ftp/admin/output.xml");
        
        Assert.assertTrue(file.exists());
        
        file.delete();
    }
    
    private String getCurrentPathName()
    {
        return getClass().getClassLoader().getResource(".").getFile();
    }
     
    private FtpServer createFtpServer()
    {
        Md5PasswordEncryptor encryptor = new Md5PasswordEncryptor();
        File usersFile = new File("user.properties");
        PropertiesUserManager propertiesUserManager = new PropertiesUserManager(encryptor, usersFile, "admin");
        FtpServerFactory serverFactory = new FtpServerFactory();
        serverFactory.setUserManager(propertiesUserManager);
        ListenerFactory factory = new ListenerFactory();
        factory.setPort(2222);
        
        serverFactory.addListener("default", factory.createListener());
        return serverFactory.createServer();
    }
    
    private Transport createFtpTransport(Map<String, String> parameters)
    {
        Transport result = new FtpTransport(new BaseConfigurable(parameters));
        return result;
    }
    
    private Transport createFileTransport(Map<String, String> parameters)
    {
        Transport result = new FileTransport(new BaseConfigurable(Configurable.BOUND_OUT, parameters));
        return result;
    }
}
