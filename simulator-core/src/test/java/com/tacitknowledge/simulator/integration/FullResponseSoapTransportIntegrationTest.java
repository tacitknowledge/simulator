package com.tacitknowledge.simulator.integration;

import com.tacitknowledge.simulator.*;
import com.tacitknowledge.simulator.camel.RouteManagerImpl;
import com.tacitknowledge.simulator.formats.FullResponseSoapAdapter;
import com.tacitknowledge.simulator.formats.SoapAdapter;
import com.tacitknowledge.simulator.impl.ConversationFactory;
import com.tacitknowledge.simulator.impl.ScenarioFactory;
import com.tacitknowledge.simulator.transports.HttpTransport;
import com.tacitknowledge.simulator.transports.SoapTransport;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import javax.xml.soap.*;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import static org.junit.Assert.assertEquals;

/**
 * @author Daniel Valencia (mailto:dvalencia@tacitknowledge.com)
 *
 * This class is used to test the Soap Transport in the context of our simulator environment.
 */
public class FullResponseSoapTransportIntegrationTest {
    Transport inTransport;
    Transport outTransport;
    Adapter inAdapter;
    Adapter outAdapter;
    Conversation conv;
    RouteManager routeManager;

    SOAPConnection connection;

    private static final String WSDL = TestFileIOHelper.GIVEX_FILES_PATH + "gapi_full.wsdl";

    static Map<String, String> inboundParams = new HashMap<String, String>();
    static {


        inboundParams.put(ConversationFactory.PASS_RATE,"1.0");
        inboundParams.put(ConversationFactory.DEMAND_TIME,"1000");
        inboundParams.put(ConversationFactory.SERVICE_TIMEOUT,"1200");
        inboundParams.put(ConversationFactory.POOL_CAPACITY,"2");
        inboundParams.put(ConversationFactory.MAX_QUEUE,"3");


        inboundParams.put("type","http");
        inboundParams.put("format","soapFullResponse");
        inboundParams.put("host","localhost");
        inboundParams.put("port","7000");
        inboundParams.put("resourceURI","/1.0/trans");
        inboundParams.put("wsdlURL", WSDL);
        inboundParams.put("isSSL","false");
    }
    static Map<String, String> outboundParams = new HashMap<String, String>();
    static {
        outboundParams.put(HttpTransport.PARAM_HTTP_OUT, "true");
        outboundParams.put("type", "http");
        outboundParams.put("format", "soapFullResponse");
        outboundParams.put("wsdlURL", WSDL);
    }



    //success case data
    private static final String SOAP_FILE_SUCCESS = "givex_get_balance.xml";
    private static final String CRITERIA_SUCCESS = "payload.GetBalance != null "
            + "&& payload.GetBalance.givexNumber.indexOf(\"111122223333\")==0";
    /**
     * set this up in a static private method at end of class to make beginning of class more readable
     */
    private static final String EXECUTION_SUCCESS = checkBalanceExecutionScript();


    //fault case data
    private static final String SOAP_FILE_FAULT = "givex_pre_auth_fail.xml";
    private static final String CRITERIA_FOR_FAULT = "payload.PreAuth != null " +
            "&& payload.PreAuth.givexNumber.indexOf(\"1111222233330000\") == 0";
    /**
     * set this up in a static private method at end of class to make beginning of class more readable
     */
    private static final String EXECUTION_FAULT = checkFaultExecutionScript();



    private static final String DESTINATION = "http://localhost:7000/1.0/trans";

    @Before
    public void setup() throws Exception
    {
        inTransport = new SoapTransport();
        outTransport = new SoapTransport();
        inAdapter = new SoapAdapter();
        outAdapter = new SoapAdapter();
        routeManager = new RouteManagerImpl();
        routeManager.start();
        //Create the soap connection
        SOAPConnectionFactory soapConnFactory = SOAPConnectionFactory.newInstance();
        connection = soapConnFactory.createConnection();
    }

    @After
    public void teardown()
    {
        try
        {
            routeManager.deactivate(conv);
            routeManager.stop();
            connection.close();
            
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }

    @SuppressWarnings("rawtypes")
    @Test
    public void successfulSoapTest() throws Exception
    {
        String criteriaScript  = CRITERIA_SUCCESS;
        String executionScript = EXECUTION_SUCCESS;
        setupConversation(criteriaScript, executionScript);

        //Make web service call
        SOAPMessage reply = connection.call(createMessage(SOAP_FILE_SUCCESS), DESTINATION);

        //Test Validation Section
        SOAPBody body = reply.getSOAPBody();
        Iterator iter = body.getChildElements();
        SOAPElement element;
        while (iter.hasNext())
        {
            element = (SOAPElement) iter.next();

            // --- As response, we expect the method name + "Response"
            assertEquals("gvxTrans:Balance", element.getTagName());
            Iterator childIter = element.getChildElements();

            //check certBalance to see if it matched value set in execution script
            element = (SOAPElement) childIter.next();
            assertEquals("certBalance", element.getTagName());
            assertEquals("1111", element.getValue());



        }
    }
    @SuppressWarnings("rawtypes")
    @Test
    public void faultPreAuthSoapTest() throws Exception
    {
        String criteriaScript  = CRITERIA_FOR_FAULT;
        String executionScript = EXECUTION_FAULT;
        setupConversation(criteriaScript, executionScript);

        //Make web service call
        SOAPMessage reply = connection.call(createMessage(SOAP_FILE_FAULT), DESTINATION);

        //Test Validation Section
        SOAPBody body = reply.getSOAPBody();
        Iterator iter = body.getChildElements();
        SOAPElement element;
        element = (SOAPElement) iter.next();

        // --- As a fault we expect a Fault in the aliased SOAP namespace
        assertEquals("SOAP-ENV:Fault", element.getTagName());
    }

    private SOAPMessage createMessage(String messageFile) throws Exception
    {
        //Create the Soap message
        MessageFactory messageFactory =
                MessageFactory.newInstance(SOAPConstants.SOAP_1_1_PROTOCOL);
        InputStream is = new ByteArrayInputStream(
                TestFileIOHelper.readFile(TestFileIOHelper.GIVEX_FILES_PATH + messageFile).getBytes("UTF-8"));

        SOAPMessage message = messageFactory.createMessage(null, is);

        //Print message, for debugging
        message.writeTo(System.out);

        return message;
    }

    private void setupTransportsAndFormats()
    {
        BaseConfigurable inTconfigurable = new BaseConfigurable(inboundParams);
        inTransport = new SoapTransport(inTconfigurable);
        BaseConfigurable inAdapterConfigurable = new BaseConfigurable();
        inAdapterConfigurable.setParameters(inboundParams);
        inAdapter = new FullResponseSoapAdapter(inAdapterConfigurable);


        BaseConfigurable outTransportConfigurable = new BaseConfigurable(Configurable.BOUND_OUT,outboundParams);
        outTransport = new SoapTransport(outTransportConfigurable);

        BaseConfigurable outConfiguration = new BaseConfigurable();
        outConfiguration.setBoundAndParameters(Configurable.BOUND_OUT, outboundParams);
        outAdapter = new FullResponseSoapAdapter(outConfiguration);
    }

    private void setupConversation(String criteriaScript, String executionScript)
            throws Exception
    {
        setupTransportsAndFormats();
        ScenarioFactory scenarioFactory = new ScenarioFactory();
        ConversationFactory conversationFactory = new ConversationFactory();
        
        conv = conversationFactory.createConversation("Soap conversation",
                                                      inTransport,
                                                      outTransport,
                                                      inAdapter,
                                                      outAdapter);
        Scenario scenario = scenarioFactory.createScenario("file.scn", "javascript", criteriaScript, executionScript);
        conv.addScenario(scenario);

        //Activate the route
        routeManager.activate(conv);
    }

    private static String checkBalanceExecutionScript() {
        return "var port = payload.GetBalance.response;\n" +
                "port.responseContent = '<SOAP-ENV:Envelope xmlns:SOAP-ENV=\"http://schemas.xmlsoap.org/soap/envelope/\" \\\n" +
                "xmlns:gvxGlobal=\"https://gapi.givex.com/1.0/types_global\" \\\n" +
                "xmlns:gvxTrans=\"https://gapi.givex.com/1.0/types_trans\" \\\n" +
                "SOAP-ENV:encodingStyle=\"http://schemas.xmlsoap.org/soap/encoding/\">\\\n" +
                "<SOAP-ENV:Body>\\\n" +
                "<gvxTrans:Balance>\\\n" +
                "<certBalance>' + payload.GetBalance.givexNumber.substr(0,4) + '</certBalance>\\\n" +
                "<pointsBalance></pointsBalance>\\\n" +
                "<expiryDate></expiryDate>\\\n" +
                "<securityCode>' + payload.GetBalance.givexNumber.substr(0, 4) + '</securityCode>\\\n" +
                "</gvxTrans:Balance>\\\n" +
                "</SOAP-ENV:Body>\\\n" +
                "</SOAP-ENV:Envelope>';\n" +
                "\n" +
                "payload;";
    }

    private static String checkFaultExecutionScript() {
        return "var port = payload.PreAuth.response;\n" +
                "port.responseContent = '<SOAP-ENV:Envelope xmlns:SOAP-ENV=\"http://schemas.xmlsoap.org/soap/envelope/\" \\\n" +
                "SOAP-ENV:encodingStyle=\"http://schemas.xmlsoap.org/soap/encoding/\">\\\n" +
                "<SOAP-ENV:Body>\\\n" +
                "<SOAP-ENV:Fault>\\\n" +
                "<faultcode>SOAP-ENV:Server</faultcode>\\\n" +
                "<faultstring>TransErr</faultstring>\\\n" +
                "<detail xmlns:ns1=\"https://gapi.givex.com/1.0/types_global\">\\\n" +
                "<ns1:GivexFaultMessage>\\\n" +
                "<message>Failed to authenticate gift card</message>\\\n" +
                "</ns1:GivexFaultMessage>\\\n" +
                "</detail>\\\n" +
                "</SOAP-ENV:Fault>\\\n" +
                "</SOAP-ENV:Body>\\\n" +
                "</SOAP-ENV:Envelope>';\n" +
                "\n" +
                "payload;";
    }


}
