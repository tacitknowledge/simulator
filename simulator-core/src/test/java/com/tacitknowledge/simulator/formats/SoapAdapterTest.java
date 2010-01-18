package com.tacitknowledge.simulator.formats;

import com.tacitknowledge.simulator.ConfigurableException;
import com.tacitknowledge.simulator.FormatAdapterException;
import com.tacitknowledge.simulator.SimulatorPojo;
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

    public void testSuccessfulAdaptFrom()
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

    public void testAdaptFromWithWrongMethod()
    {
        testAdaptFromShouldFail(
                testFileName.append(SOAP_FILE_WITH_WRONG_METHOD).toString()
        );
    }

    public void testAdaptFromWithWrongParameter()
    {
        testAdaptFromShouldFail(
                testFileName.append(SOAP_FILE_WITH_WRONG_PARAM).toString());
    }

    private void testAdaptFromShouldFail(String fileName)
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

    private void setExchangeContentsFromFile(String fileName)
            throws Exception
    {
        message.setBody(
                    TestHelper.readFile(fileName));        
        exchange.setIn(message);
    }

    /*
    public void testGetServiceFromWSDL()
    {
        InputStream wsdlStream = getClass().getResourceAsStream("/HelloService.wsdl");

        assertNotNull(wsdlStream);

        try
        {
            WSDLFactory wsdlFactory = WSDLFactory.newInstance();
            WSDLReader wsdlReader = wsdlFactory.newWSDLReader();

            wsdlReader.setFeature("javax.wsdl.verbose", false);
            wsdlReader.setFeature("javax.wsdl.importDocuments", true);

            Definition definition = wsdlReader.readWSDL(null, new InputSource(wsdlStream));
            if (definition == null)
            {
                System.err.println("definition element is null");
                System.exit(1);
                fail("Definition element is null");
            }

            // find service
            Map servicesMap = definition.getServices();
            assertNotNull(servicesMap);

            // --- Make sure we got the "Hello_Service" service
            Service service = (Service) servicesMap.get(
                            new QName(definition.getTargetNamespace(), "Hello_Service"));
            assertNotNull(service);

            Map<QName, Binding> bindings = definition.getBindings();

            // --- Available operations
            List<String> availableOps = new ArrayList<String>();
            for (Map.Entry<QName, Binding> entry : bindings.entrySet())
            {
                System.out.println("BINDING: " + entry.getValue().getQName());

                for (ExtensibilityElement ee :
                        (List<ExtensibilityElement>) entry.getValue().getExtensibilityElements())
                {
                    if (ee instanceof SOAPBinding)
                    {
                        System.out.println("SOAP binding: " + ((SOAPBinding) ee).getTransportURI());
                    }
                }

                List<BindingOperation> operations = entry.getValue().getBindingOperations();

                for (BindingOperation op : operations)
                {
                    System.out.println("* Available Operation name: " + op.getName());

                    // --- Show the expected input and output
                    System.out.println("   - Operation Input:");
                    Map<String, Part> parts = op.getOperation().getInput().getMessage().getParts();
                    for (Map.Entry<String, Part> partEntry : parts.entrySet())
                    {
                        System.out.println("      Parameter: " + partEntry.getValue().getName());
                    }

                    System.out.println("   - Operation Output:");
                    parts = op.getOperation().getOutput().getMessage().getParts();
                    for (Map.Entry<String, Part> partEntry : parts.entrySet())
                    {
                        System.out.println("      Parameter: " + partEntry.getValue().getName());
                    }
                }
            }
        }
        catch (WSDLException we)
        {
            we.printStackTrace();
            fail(we.getMessage());
        }
    }
    */
}
