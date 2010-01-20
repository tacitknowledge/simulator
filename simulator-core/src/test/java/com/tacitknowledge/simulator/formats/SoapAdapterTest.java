package com.tacitknowledge.simulator.formats;

import com.tacitknowledge.simulator.Configurable;
import com.tacitknowledge.simulator.ConfigurableException;
import com.tacitknowledge.simulator.FormatAdapterException;
import com.tacitknowledge.simulator.SimulatorPojo;
import com.tacitknowledge.simulator.StructuredSimulatorPojo;
import com.tacitknowledge.simulator.TestHelper;
import junit.framework.TestCase;
import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.impl.DefaultExchange;
import org.apache.camel.impl.DefaultMessage;

import java.util.HashMap;
import java.util.Map;

/**
 * @author galo
 */
public class SoapAdapterTest extends TestCase
{
    public static final String WSDL_FILE = "HelloService.wsdl";

    public static final String SOAP_FILE = "soap_test.xml";
    public static final String SOAP_FILE_WITH_WRONG_METHOD = "soap_test_wrong_method.xml";
    public static final String SOAP_FILE_WITH_WRONG_PARAM = "soap_test_wrong_param.xml";

    private Exchange exchange;
    private Message message;

    private SoapAdapter adapter;
    private String testWSDLFileName = TestHelper.RESOURCES_PATH + WSDL_FILE;
    private StringBuilder testFileName;

    /**
     * Sets up the fixture, for example, open a network connection.
     * This method is called before a test is executed.
     */
    @Override
    protected void setUp() throws Exception
    {
        super.setUp();

        CamelContext context = new DefaultCamelContext();
        exchange = new DefaultExchange(context);
        message = new DefaultMessage();

        adapter = new SoapAdapter();
        testFileName = new StringBuilder(TestHelper.ORIGINAL_FILES_PATH );

        Map<String, String> params = new HashMap<String, String>();
        params.put(SoapAdapter.PARAM_WSDL_URL, testWSDLFileName);
        adapter.setParameters(params);
        adapter.validateParameters();
    }

    public void testShouldFailWithWrongWsdlUri()
    {
        Map<String, String> pars = new HashMap<String, String>();
        pars.put(SoapAdapter.PARAM_WSDL_URL, "noWSDLFileHere.wsdl");
        adapter.setParameters(pars);

        try
        {
            adapter.validateParameters();
            fail("Expecting exception from wrong WSDL location");
        }
        catch(ConfigurableException ce)
        {
            // --- This is OK
        }
    }

    public void testSuccessfulCreateSimulatorPojo()
    {
        try
        {
            setExchangeContentsFromFile(
                    testFileName.append(SOAP_FILE).toString());

            // --- Get a SimulatorPojo from our fake little XML
            SimulatorPojo pojo = adapter.createSimulatorPojo(exchange);

            // --- Assert the pojo has a root
            assertNotNull(pojo.getRoot());
            // --- Assert we got the SOAP payload
            assertNotNull(pojo.getRoot().get(SoapAdapter.DEFAULT_PAYLOAD_KEY));
            Map<String, Object> payload = (Map<String, Object>) pojo.getRoot().get(SoapAdapter.DEFAULT_PAYLOAD_KEY);

            assertTrue("Expecting 'sayHello' method in payload", payload.containsKey("sayHello"));
            Map<String, Object> sayHello = (Map<String, Object>) payload.get("sayHello");
            assertTrue("Expecting 'firstName' param in 'sayHello' method", sayHello.containsKey("firstName"));
        }
        catch (Exception e)
        {
            e.printStackTrace();
            fail();
        }
    }

    public void testCreateSimulatorPojoWithWrongMethod()
    {
        testCreateSimulatorPojoShouldFail(
                testFileName.append(SOAP_FILE_WITH_WRONG_METHOD).toString()
        );
    }

    public void testCreateSimulatorPojoWithWrongParameter()
    {
        testCreateSimulatorPojoShouldFail(
                testFileName.append(SOAP_FILE_WITH_WRONG_PARAM).toString());
    }

    public void testSuccessfulGetString()
    {
        setupOutboundSoapAdapter();

        SimulatorPojo pojo = new StructuredSimulatorPojo();
        pojo.getRoot().put(
                SoapAdapter.DEFAULT_PAYLOAD_KEY,
                TestHelper.getMapOneEntry(
                    "sayHello",
                    TestHelper.getMapOneEntry(
                            "greeting",
                            "Hello there, Dude!"))
                );

        // --- Now invoke the getString method
        try
        {
            String soapMessage = adapter.getString(pojo, exchange).toString();

            System.out.println(soapMessage);
        }
        catch(FormatAdapterException fae)
        {
            fae.printStackTrace();
            fail("Not expecting exception, yet got: " + fae.getMessage());
        }
    }

    public void testGetStringWithWrongMethod()
    {
        setupOutboundSoapAdapter();

        SimulatorPojo pojo = new StructuredSimulatorPojo();
        pojo.getRoot().put(
                SoapAdapter.DEFAULT_PAYLOAD_KEY,
                TestHelper.getMapOneEntry(
                    "sayHi",
                    TestHelper.getMapOneEntry(
                            "greeting",
                            "Hello there, Dude!"))
                );

        testGetStringShouldFail(pojo);
    }

    public void testGetStringWithWrongParameter()
    {
        setupOutboundSoapAdapter();

        SimulatorPojo pojo = new StructuredSimulatorPojo();
        pojo.getRoot().put(
                SoapAdapter.DEFAULT_PAYLOAD_KEY,
                TestHelper.getMapOneEntry(
                    "sayHello",
                    TestHelper.getMapOneEntry(
                            "regards",
                            "Hello there, Dude!"))
                );

        testGetStringShouldFail(pojo);
    }

    private void testCreateSimulatorPojoShouldFail(String fileName)
    {
        try
        {
            setExchangeContentsFromFile(fileName);
        }
        catch(Exception e)
        {
            e.printStackTrace();
            fail(e.getMessage());
        }

        try
        {
            adapter.createSimulatorPojo(exchange);
            fail("Expecting FormatAdapterException from bad SOAP message");
        }
        catch (FormatAdapterException fae)
        {
            // --- This is OK
        }
    }

    private void testGetStringShouldFail(SimulatorPojo pojo)
    {
        try
        {
            adapter.getString(pojo, exchange);
        }
        catch(FormatAdapterException fae)
        {
            // --- This is OK
        }
    }

    private void setExchangeContentsFromFile(String fileName)
            throws Exception
    {
        message.setBody(
                    TestHelper.readFile(fileName));        
        exchange.setIn(message);
    }

    private void setupOutboundSoapAdapter()
    {
        // --- Flag the adapter as outbound
        Map<String, String> pars = new HashMap<String, String>();
        pars.put(SoapAdapter.PARAM_WSDL_URL, testWSDLFileName);
        adapter.setBoundAndParameters(Configurable.BOUND_OUT, pars);
    }
}
