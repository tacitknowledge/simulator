package com.tacitknowledge.simulator.transports;

import static org.junit.Assert.assertEquals;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.xml.soap.MessageFactory;
import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPConnection;
import javax.xml.soap.SOAPConnectionFactory;
import javax.xml.soap.SOAPConstants;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.tacitknowledge.simulator.Adapter;
import com.tacitknowledge.simulator.Configurable;
import com.tacitknowledge.simulator.Conversation;
import com.tacitknowledge.simulator.RouteManager;
import com.tacitknowledge.simulator.TestHelper;
import com.tacitknowledge.simulator.Transport;
import com.tacitknowledge.simulator.camel.RouteManagerImpl;
import com.tacitknowledge.simulator.formats.SoapAdapter;
import com.tacitknowledge.simulator.impl.ConversationFactory;

/**
 * @author Daniel Valencia (mailto:dvalencia@tacitknowledge.com)
 *
 * This class is used to test the Soap Transport in the context of our simulator environment.
 */
public class SoapTransportIntegrationTest {
    Transport inTransport;
    Transport outTransport;
    Adapter inAdapter;
    Adapter outAdapter;
    Conversation conv;
    RouteManager routeManager;

    SOAPConnection connection;

    private static final String SOAP_FILE = "soap_test.xml";
    private static final String COMPLEX_SOAP_FILE = "complex_soap_test.xml";

    private String testWSDLFileName = TestHelper.RESOURCES_PATH + "HelloService.wsdl";
    private String complexWSDLFileName = TestHelper.RESOURCES_PATH + "OrderService.wsdl";

    private static final String DESTINATION = "http://0.0.0.0:7000/soapService";
    private static final String RESPONSE_GREETING = "HELLLLLLLOOWWWWWW";

    @Before
    public void setup() throws SOAPException
    {
        inTransport = new SoapTransport();
        outTransport = new SoapTransport();
        inAdapter = new SoapAdapter();
        outAdapter = new SoapAdapter();
        routeManager = new RouteManagerImpl();

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
        String criteriaScript  = "payload.sayHello.firstName == 'Dude'";
        String executionScript = "payload.sayHello.greeting = '" +
                RESPONSE_GREETING + "'; payload;";
        setupConversation(testWSDLFileName, criteriaScript, executionScript);

        //Make web service call
        SOAPMessage reply = connection.call(createMessage(SOAP_FILE), DESTINATION);

        SOAPBody body = reply.getSOAPBody();
        Iterator iter = body.getChildElements();
        SOAPElement element;
        while (iter.hasNext())
        {
            element = (SOAPElement) iter.next();

            // --- As response, we expect the method name + "Response"
            assertEquals("tns:sayHelloResponse", element.getTagName());
            Iterator childIter = element.getChildElements();
            while (childIter.hasNext())
            {
                element = (SOAPElement) childIter.next();
                assertEquals("tns:greeting", element.getTagName());
                assertEquals(RESPONSE_GREETING, element.getValue());
                break;
            }
            break;
        }
    }

    @SuppressWarnings("rawtypes")
    @Test
    public void successfulComplexSoapTest() throws Exception
    {
        String criteriaScript = "payload.updateOrderItemsForShipping.order.id == '8653216'";
        String executionScript =
                "payload.updateOrderItemsForShipping.order.itemsForShipping = " +
                    "payload.updateOrderItemsForShipping.order.itemsForShipping - " +
                "payload.updateOrderItemsForShipping.shippedItems.item.length;" +
                "payload;";
        setupConversation(complexWSDLFileName, criteriaScript, executionScript);

        //Make web service call
        SOAPMessage reply = connection.call(createMessage(COMPLEX_SOAP_FILE), DESTINATION);

        SOAPBody body = reply.getSOAPBody();
        Iterator iter = body.getChildElements();
        SOAPElement element;
        while (iter.hasNext())
        {
            element = (SOAPElement) iter.next();

            // --- As response, we expect the method name + "Response"
            assertEquals("tns:updateOrderItemsForShippingResponse", element.getTagName());
            Iterator childIter = element.getChildElements();
            while (childIter.hasNext())
            {
                element = (SOAPElement) childIter.next();
                assertEquals("tns:order", element.getTagName());

                Iterator orderChildren = element.getChildElements();
                while (orderChildren.hasNext())
                {
                    SOAPElement orderChild = (SOAPElement) orderChildren.next();
                    if (orderChild.getTagName().equals("id"))
                    {
                        assertEquals("8653216", orderChild.getValue());
                    } else if (orderChild.getTagName().equals("itemsForShipping"))
                    {
                        assertEquals("2", orderChild.getValue());
                    }
                }
                break;
            }
            break;
        }
    }

    @SuppressWarnings("rawtypes")
    @Test
    public void getSOAPFaultResponseTest() throws Exception
    {
        String criteriaScript  = "payload.sayHello.firstName == 'Dude'";
        String executionScript =
                "payload.fault." + SoapAdapter.FAULT_STRING + " = 'Who are you, Dude?!';" +
                "payload;";
        setupConversation(testWSDLFileName, criteriaScript, executionScript);

        //Make web service call
        SOAPMessage reply = connection.call(createMessage(SOAP_FILE), DESTINATION);

        //Print message, for debugging
        reply.writeTo(System.out);

        SOAPBody body = reply.getSOAPBody();
        Iterator iter = body.getChildElements();
        SOAPElement element;
        while (iter.hasNext())
        {
            element = (SOAPElement) iter.next();

            // --- As response, expect a Fault
            assertEquals("env:Fault", element.getTagName());
            Iterator childIter = element.getChildElements();
            while (childIter.hasNext())
            {
                element = (SOAPElement) childIter.next();
                String tagName = element.getTagName();

                // --- Expect faultCode and faultString
                if (tagName.equals("env:" + SoapAdapter.FAULT_CODE))
                {
                    assertEquals("env:Sender", element.getValue());
                }
                else if (tagName.equals("env:" + SoapAdapter.FAULT_STRING))
                {
                    assertEquals("Who are you, Dude?!", element.getValue());
                }
            }
            break;
        }
    }

    private SOAPMessage createMessage(String messageFile) throws Exception
    {
        //Create the Soap message
        MessageFactory messageFactory =
                MessageFactory.newInstance(SOAPConstants.SOAP_1_2_PROTOCOL);
        InputStream is = new ByteArrayInputStream(
                TestHelper.readFile(TestHelper.ORIGINAL_FILES_PATH + messageFile).getBytes("UTF-8"));

        SOAPMessage message = messageFactory.createMessage(null, is);

        //Print message, for debugging
        message.writeTo(System.out);

        return message;
    }

    private void setupTransportsAndFormats(String wsdlFile)
    {
        Map<String, String> transportParams = new HashMap<String, String>();
        transportParams.put(HttpTransport.PARAM_RESOURCE_URI, "/soapService");
        transportParams.put(HttpTransport.PARAM_PORT, "7000");
        inTransport.setParameters(transportParams);


        Map<String, String> adapterParams = new HashMap<String, String>();
        adapterParams.put(SoapAdapter.PARAM_WSDL_URL, wsdlFile);
        inAdapter.setParameters(adapterParams);

        Map<String, String> pars = new HashMap<String, String>();
        pars.put(HttpTransport.PARAM_HTTP_OUT, "true");
        outTransport.setParameters(pars);

        outAdapter.setBoundAndParameters(Configurable.BOUND_OUT, adapterParams);
    }

    private void setupConversation(String wsdlFile, String criteriaScript, String executionScript)
            throws Exception
    {
        setupTransportsAndFormats(wsdlFile);

        conv = ConversationFactory.createConversation("Soap conversation",
                                                      inTransport,
                                                      outTransport,
                                                      inAdapter,
                                                      outAdapter);

        conv.addScenario("javascript", criteriaScript, executionScript);

        //Activate the route
        routeManager.activate(conv);
    }
}
