package com.tacitknowledge.simulator.formats;

import com.tacitknowledge.simulator.Adapter;
import com.tacitknowledge.simulator.FormatAdapterException;
import com.tacitknowledge.simulator.SimulatorPojo;
import com.tacitknowledge.simulator.StructuredSimulatorPojo;
import com.tacitknowledge.simulator.configuration.ParameterDefinitionBuilder;
import org.apache.camel.Exchange;
import org.apache.log4j.Logger;
import org.xml.sax.InputSource;

import javax.wsdl.Binding;
import javax.wsdl.BindingOperation;
import javax.wsdl.Definition;
import javax.wsdl.WSDLException;
import javax.wsdl.factory.WSDLFactory;
import javax.wsdl.xml.WSDLReader;
import javax.xml.namespace.QName;
import javax.xml.soap.*;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Map;

import static com.tacitknowledge.simulator.configuration.ParametersListBuilder.parameters;
import static com.tacitknowledge.simulator.configuration.ParameterDefinitionBuilder.name;

/**
 * @author galo
 */
public class SoapAdapter extends XmlAdapter implements Adapter<Object>
{
    /**
     * WSDL URL parameter. OPTIONAL.
     */
    public static final String PARAM_WSDL_URL = "wdslURL";

    /**
     * Logger for this class.
     */
    private static Logger logger = Logger.getLogger(SoapAdapter.class);

    /**
     * Adapter parameters definition.
     */
    private List<ParameterDefinitionBuilder.ParameterDefinition> parametersList =
            parameters().
                    add(
                            name(PARAM_WSDL_URL).
                                    label("WSDL URL").
                                    required()
                    );

    /**
     * WSDL service definition.
     * Will be generated from the provided WSDL.
     */
    private Definition definition;

    /**
     * Available operations defined in the provided WSDL.
     */
    private Map<String, BindingOperation> availableOps;

    /**
     * Constructor
     */
    public SoapAdapter()
    {
        super(false);
    }

    /**
     * Constructor
     *
     * @param parameters @see #parameters
     */
    public SoapAdapter(Map<String, String> parameters)
    {
        super(parameters, false);
    }

    protected SimulatorPojo createSimulatorPojo(String o)
        throws FormatAdapterException
    {
        logger.debug("Attempting to generate SimulatorPojo from SOAP content:\n" + o);

        SimulatorPojo pojo = new StructuredSimulatorPojo();

        try
        {
            MessageFactory factory = MessageFactory.newInstance(SOAPConstants.SOAP_1_2_PROTOCOL);

            InputStream is = new ByteArrayInputStream(o.getBytes("UTF-8"));
            // TODO - SO WHAT ABOUT THE HEADERS?
            SOAPMessage message = factory.createMessage(null, is);

            // --- So, now we got the SOAP message parsed.
            SOAPBody body = message.getSOAPBody();

            pojo.getRoot().put("payload", getStructuredChilds(body));
        } catch(SOAPException se)
        {
            String errorMessage = "Unexpected SOAP exception";
            logger.error(errorMessage, se);
            throw new FormatAdapterException(errorMessage, se);
        } catch(UnsupportedEncodingException uee)
        {
            String errorMessage = "Unsupported encoding exception";
            logger.error(errorMessage, uee);
            throw new FormatAdapterException(errorMessage, uee);
        } catch (IOException ioe)
        {
            String errorMessage = "Unexpected IO exception";
            logger.error(errorMessage, ioe);
            throw new FormatAdapterException(errorMessage, ioe);
        }

        return pojo;
    }

    @Override
    public SimulatorPojo createSimulatorPojo(Exchange o) throws FormatAdapterException
    {
        return createSimulatorPojo(o.getIn().getBody(String.class));
    }

    /**
     * Sets and/or overrides instance variables from provided parameters and validates that
     * the required parameters are present.
     *
     * @throws com.tacitknowledge.simulator.FormatAdapterException
     *          If any required parameter is missing or incorrect
     */
    @Override
    void validateParameters() throws FormatAdapterException
    {
        if (getParamValue(PARAM_WSDL_URL) == null)
        {
            throw new FormatAdapterException("WSDL URL parameter is required");
        }
    }

    /**
     * @return List of Paramaters for the implementing Adapter.
     */
    public List<List> getParametersList()
    {
        return getParametersDefinitionsAsList(parametersList);
    }

    private void getWSDLDefinition() throws FormatAdapterException {
        try
        {
            WSDLFactory wsdlFactory = WSDLFactory.newInstance();
            WSDLReader wsdlReader = wsdlFactory.newWSDLReader();

            //wsdlReader.setFeature("javax.wsdl.verbose", false);
            //wsdlReader.setFeature("javax.wsdl.importDocuments", true);

            definition = wsdlReader.readWSDL(getParamValue(PARAM_WSDL_URL));
            if (definition == null)
            {
                throw new FormatAdapterException(
                        "Definition element is null for WSDL URL: " +
                        getParamValue(PARAM_WSDL_URL));
            }
        }
        catch(WSDLException we)
        {
            String errorMsg = "Unexpected WSDL error: " + we.getMessage();
            logger.error(errorMsg, we);
            throw new FormatAdapterException(errorMsg, we);
        }
    }

    private void getAvailableOperations()
    {
        Map<QName, Binding> bindings = definition.getBindings();

        for (Map.Entry<QName, Binding> entry : bindings.entrySet())
        {
            List<BindingOperation> operations = entry.getValue().getBindingOperations();
            for (BindingOperation op : operations)
            {
                availableOps.put(op.getName(), op);
            }
        }
    }
}
