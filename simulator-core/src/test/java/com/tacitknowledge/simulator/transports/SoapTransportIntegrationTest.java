package com.tacitknowledge.simulator.transports;

import org.apache.camel.CamelContext;
import org.junit.Before;
import org.junit.Test;
import org.junit.Ignore;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import com.tacitknowledge.simulator.*;
import com.tacitknowledge.simulator.impl.ConversationImpl;
import com.tacitknowledge.simulator.camel.RouteManagerImpl;
import com.tacitknowledge.simulator.formats.SoapAdapter;

import javax.xml.soap.*;
import java.util.Map;
import java.util.HashMap;
import java.util.Iterator;
import java.io.InputStream;
import java.io.ByteArrayInputStream;

/**
 * @author Daniel Valencia (mailto:dvalencia@tacitknowledge.com)
 *
 * This class is used to test the Soap Transport in the context of our simulator environment.
 */
public class SoapTransportIntegrationTest {
    CamelContext context;
    Transport inTransport;
    Transport outTransport;
    Adapter inAdapter;
    Adapter outAdapter;
    RouteManager routeManager;

    private static final String SOAP_FILE = "soap_test.xml";

    private String testWSDLFileName = TestHelper.RESOURCES_PATH + "/HelloService.wsdl";

    String destination = "http://0.0.0.0:7000/soapService";
    private static final String RESPONSE_GREETING = "HELLLLLLLOOWWWWWW";

    @Before
    public void setup(){
        inTransport = TransportFactory.getInstance().getTransport(TransportConstants.SOAP);
        outTransport = TransportFactory.getInstance().getTransport(TransportConstants.SOAP);
        inAdapter = new SoapAdapter();
        outAdapter = new SoapAdapter();
        routeManager = new RouteManagerImpl();
    }

    @Ignore("Ignoring until soap transport is ready.")
    @Test
    public void succesfulSoapTest() throws Exception{

        Map<String, String> transportParams = new HashMap<String, String>();
        transportParams.put(HttpTransport.PARAM_RESOURCE_URI, "/soapService");
        transportParams.put(HttpTransport.PARAM_PORT, "7000");
        inTransport.setParameters(transportParams);


        Map<String, String> adapterParams = new HashMap<String, String>();
        adapterParams.put(SoapAdapter.PARAM_WSDL_URL, testWSDLFileName);
        inAdapter.setParameters(adapterParams);

        Map<String, String> pars = new HashMap<String, String>();
        pars.put(HttpTransport.PARAM_HTTP_OUT, "true");
        outTransport.setParameters(pars);

        outAdapter.setBoundAndParameters(Configurable.BOUND_OUT, adapterParams);

        Conversation conv = new ConversationImpl(1, "Soap conversation", inTransport, outTransport, inAdapter, outAdapter, "");
        String criteriaScript       = "payload.sayHello.firstName == 'Dude'";
        String transformationScript = "payload.sayHello.greeting = '" + RESPONSE_GREETING + "'; payload;";
        
        conv.addOrUpdateScenario(1,"javascript", criteriaScript, transformationScript);

        //Activate the route
        routeManager.activate(conv);

        //Create the soap connection
        SOAPConnectionFactory soapConnFactory = SOAPConnectionFactory.newInstance();
        SOAPConnection connection = soapConnFactory.createConnection();

        //Create the Soap message
        MessageFactory messageFactory = MessageFactory.newInstance(SOAPConstants.SOAP_1_2_PROTOCOL);
        InputStream is = new ByteArrayInputStream(TestHelper.readFile(TestHelper.ORIGINAL_FILES_PATH + SOAP_FILE).getBytes("UTF-8"));

        SOAPMessage message = messageFactory.createMessage(null, is);

        //Print message, for debugging
        message.writeTo(System.out);

        //Make web service call
        SOAPMessage reply = connection.call(message, destination);

        SOAPBody body = reply.getSOAPBody();

        Iterator iter = body.getChildElements();
        SOAPElement element;
        while(iter.hasNext()){
            element = (SOAPElement)iter.next();
            assertEquals("tns:sayHello", element.getElementQName());
            Iterator childIter = element.getChildElements();
            while(childIter.hasNext()){
                element = (SOAPElement)childIter.next();
                assertEquals("tns:greeting", element.getElementQName());
                assertEquals(RESPONSE_GREETING,element.getValue());
                break;
            }
            break;
        }
        //Close the connection
        connection.close();

        routeManager.deactivate(conv);
    }


}
