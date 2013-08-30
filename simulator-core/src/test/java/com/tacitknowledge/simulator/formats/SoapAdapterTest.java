package com.tacitknowledge.simulator.formats;

import com.tacitknowledge.simulator.*;
import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.impl.DefaultExchange;
import org.apache.camel.impl.DefaultMessage;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import static org.junit.Assert.fail;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.HashMap;
import java.util.Map;

/**
 * @author galo
 */
public class SoapAdapterTest
{
    public static final String WSDL_FILE = "HelloService.wsdl";
    public static final String COMPLEX_WSDL_FILE = "OrderService.wsdl";

    public static final String SOAP_FILE = "soap_test.xml";
    public static final String COMPLEX_SOAP_FILE = "complex_soap_test.xml";
    public static final String SOAP_FILE_WITH_WRONG_METHOD = "soap_test_wrong_method.xml";
    public static final String SOAP_FILE_WITH_WRONG_PARAM = "soap_test_wrong_param.xml";

    private Exchange exchange;
    private Message message;

    private Map<String, String> params;
    private SoapAdapter adapter;
    private String defaultWSDLFileName = TestFileIOHelper.RESOURCES_PATH + WSDL_FILE;
    private StringBuilder testFileName;

    /**
     * Sets up the fixture, for example, open a network connection.
     * This method is called before a test is executed.
     */
    @Before
    public void setUp() throws Exception
    {
        CamelContext context = new DefaultCamelContext();
        exchange = new DefaultExchange(context);
        message = new DefaultMessage();

        adapter = new SoapAdapter();
        setupSoapAdapter();

        testFileName = new StringBuilder(TestFileIOHelper.ORIGINAL_FILES_PATH);
    }

    @Ignore("may remove this test and delay resource download failure until later.  " +
            "fail fast decision is pending, but not sure if I want to do it at construction or validation. " +
            "currently it would download the WSDL twice per call which I don't want.")
    @Test
    public void testShouldFailWithWrongWsdlUri()
    {

        params = new HashMap<String, String>();
        params.put(SoapAdapter.PARAM_WSDL_URL, "noWSDLFileHere.wsdl");
        BaseConfigurable configurable = new BaseConfigurable();
        configurable.setParameters(params);
        adapter = new SoapAdapter(configurable);

        try
        {
            adapter.validateParameters();
            fail("Expecting exception from wrong WSDL location");
        }
        catch (ConfigurableException ce)
        {
            // --- This is OK
        }
    }

    @SuppressWarnings("unchecked")
    @Test
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
            Map<String, Object> payload =
                    (Map<String, Object>) pojo.getRoot().get(SoapAdapter.DEFAULT_PAYLOAD_KEY);

            assertTrue("Expecting 'sayHello' method in payload", payload.containsKey("sayHello"));
            Map<String, Object> sayHello = (Map<String, Object>) payload.get("sayHello");
            assertTrue("Expecting 'firstName' param in 'sayHello' method",
                    sayHello.containsKey("firstName"));

            // --- We also expect to get the fault object
            assertNotNull(payload.get(SoapAdapter.FAULT));
        }
        catch (Exception e)
        {
            e.printStackTrace(System.err);
            fail();
        }
    }

    @Test
    public void testCreateSimulatorPojoWithWrongMethod()
    {
        testCreateSimulatorPojoShouldFail(
                testFileName.append(SOAP_FILE_WITH_WRONG_METHOD).toString()
        );
    }

    @Test
    public void testCreateSimulatorPojoWithWrongParameter()
    {
        testCreateSimulatorPojoShouldFail(
                testFileName.append(SOAP_FILE_WITH_WRONG_PARAM).toString());
    }

    @Test
    public void testSuccessfulGetString() throws Exception
    {
        setupSoapAdapter(Configurable.BOUND_OUT);

        SimulatorPojo pojo = new StructuredSimulatorPojo();
        pojo.getRoot().put(
                SoapAdapter.DEFAULT_PAYLOAD_KEY,
                TestHelper.getMapOneEntry(
                        "sayHello",
                        TestHelper.getMapOneEntry(
                                "greeting",
                                "Hello there, Dude!"))
        );

        // --- Now invoke the getConversationResponseAsString method
        try
        {
            String soapMessage = adapter.getConversationResponseAsString(pojo, exchange);

            System.out.println(soapMessage);
        }
        catch(FormatAdapterException fae)
        {
            fae.printStackTrace();
            fail("Not expecting exception, yet got: " + fae.getMessage());
        }
    }

    @Test
    public void testGetSoapFaultResponse() throws Exception
    {
        setupSoapAdapter(Configurable.BOUND_OUT);

        SimulatorPojo pojo = new StructuredSimulatorPojo();
        pojo.getRoot().put(
                SoapAdapter.DEFAULT_PAYLOAD_KEY,
                TestHelper.getMapOneEntry(
                    "sayHello",
                    TestHelper.getMapOneEntry(
                            "missing",
                            "Missed you!"))
                );

        // --- Now invoke the getConversationResponseAsString method
        try
        {
            String soapMessage = adapter.getConversationResponseAsString(pojo, exchange);

            System.out.println(soapMessage);
            assertTrue("Expecting env:Fault tag", soapMessage.indexOf("<env:Fault>") > -1);
        }
        catch(FormatAdapterException fae)
        {
            fae.printStackTrace();
            fail("Not expecting exception, yet got: " + fae.getMessage());
        }
    }


    public void testGetStringWithWrongMethod() throws Exception
    {
        setupSoapAdapter(Configurable.BOUND_OUT);

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

    @Test
    public void testGetStringWithWrongParameter() throws Exception
    {
        setupSoapAdapter(Configurable.BOUND_OUT);

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
            adapter.getConversationResponseAsString(pojo, exchange);
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
                TestFileIOHelper.readFile(fileName));
        exchange.setIn(message);
    }

    private void setupSoapAdapter() throws Exception
    {
        setupSoapAdapter(Configurable.BOUND_IN, defaultWSDLFileName);
    }

    private void setupSoapAdapter(int bound) throws Exception
    {
        setupSoapAdapter(bound, defaultWSDLFileName);
    }

    private void setupSoapAdapter(int bound,String wsdlFile) throws Exception
    {
        // --- Flag the adapter as outbound
        params = new HashMap<String, String>();
        params.put(SoapAdapter.PARAM_WSDL_URL, wsdlFile);
        BaseConfigurable configurable = new BaseConfigurable();
        configurable.setBoundAndParameters(bound,params);
        adapter = new SoapAdapter(configurable);

        adapter.validateParameters();
    }
}
