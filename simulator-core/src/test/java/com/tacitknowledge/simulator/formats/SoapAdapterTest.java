package com.tacitknowledge.simulator.formats;

import com.tacitknowledge.simulator.SimulatorPojo;
import com.tacitknowledge.simulator.TestHelper;
import junit.framework.TestCase;
import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.impl.DefaultExchange;
import org.apache.camel.impl.DefaultMessage;
import org.xml.sax.InputSource;

import javax.wsdl.Binding;
import javax.wsdl.BindingOperation;
import javax.wsdl.Definition;
import javax.wsdl.Part;
import javax.wsdl.Service;
import javax.wsdl.WSDLException;
import javax.wsdl.extensions.ExtensibilityElement;
import javax.wsdl.extensions.soap.SOAPBinding;
import javax.wsdl.factory.WSDLFactory;
import javax.wsdl.xml.WSDLReader;
import javax.xml.namespace.QName;
import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author galo
 */
public class SoapAdapterTest extends TestCase
{
    public static final String SOAP_FILE = "soap_test.xml";

    public void testAdaptFromSoap()
    {
        SoapAdapter adapter =
                (SoapAdapter) AdapterFactory.getInstance().getAdapter(FormatConstants.SOAP);

        SimulatorPojo pojo;
        try
        {
            CamelContext context = new DefaultCamelContext();
            Exchange exchange = new DefaultExchange(context);
            Message message = new DefaultMessage();
            message.setBody(
                    TestHelper.readFile(
                            new File(TestHelper.ORIGINAL_FILES_PATH + "/" + SOAP_FILE)));

            exchange.setIn(message);
            // --- Get a SimulatorPojo from our fake little XML
            pojo = adapter.createSimulatorPojo(exchange);

            // --- Assert the pojo has a root
            assertNotNull(pojo.getRoot());

            Map<String, Object> root = pojo.getRoot();
        }
        catch (Exception e)
        {
            e.printStackTrace();
            fail();
        }
    }

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
}
