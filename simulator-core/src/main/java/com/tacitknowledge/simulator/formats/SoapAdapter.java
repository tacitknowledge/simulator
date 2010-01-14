package com.tacitknowledge.simulator.formats;

import com.tacitknowledge.simulator.Adapter;
import com.tacitknowledge.simulator.FormatAdapterException;
import com.tacitknowledge.simulator.SimulatorPojo;
import com.tacitknowledge.simulator.StructuredSimulatorPojo;
import com.tacitknowledge.simulator.configuration.ParameterDefinitionBuilder;
import org.apache.log4j.Logger;
import org.apache.camel.Exchange;

import javax.xml.soap.MessageFactory;
import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Map;

import static com.tacitknowledge.simulator.configuration.ParametersListBuilder.parameters;

/**
 * @author galo
 */
public class SoapAdapter extends BaseAdapter implements Adapter<Object>
{
    /**
     * Logger for this class.
     */
    private static Logger logger = Logger.getLogger(SoapAdapter.class);

    /**
     * Adapter parameters definition.
     */
    private List<ParameterDefinitionBuilder.ParameterDefinition> parametersList = parameters();

    /**
     * Constructor
     */
    public SoapAdapter()
    {
    }

    /**
     * Constructor
     *
     * @param parameters @see #parameters
     */
    public SoapAdapter(Map<String, String> parameters)
    {
        super(parameters);
    }

    @Override
    protected SimulatorPojo createSimulatorPojo(Exchange o)
        throws FormatAdapterException
    {
        logger.debug("Attempting to generate SimulatorPojo from SOAP content:\n" + o);

        /*
        SimulatorPojo pojo = new StructuredSimulatorPojo();

        try
        {
            MessageFactory factory = MessageFactory.newInstance();

            InputStream is = new ByteArrayInputStream(o.getBytes("UTF-8"));
            // TODO - SO WHAT ABOUT THE HEADERS?
            SOAPMessage message = factory.createMessage(null, is);

            SOAPBody body = message.getSOAPBody();
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
        */

        return null;
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

    }

    /**
     * Returns the List of parameters the implementing instance uses.
     * Each list element is itself a List to describe the parameter as follows:
     * - 0 : Parameter name
     * - 1 : Parameter description. Useful for GUI rendition
     * - 2 : Parameter type. Useful for GUI rendition.
     * - 3 : Required or Optional parameter. Useful for GUI validation.
     *
     * @return List of Paramaters for the implementing Adapter.
     */
    public List<List> getParametersList()
    {
        return getParametersDefinitionsAsList(parametersList);
    }
}
