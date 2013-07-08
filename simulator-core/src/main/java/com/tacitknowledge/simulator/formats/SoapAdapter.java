package com.tacitknowledge.simulator.formats;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.wsdl.Binding;
import javax.wsdl.BindingOperation;
import javax.wsdl.Definition;
import javax.wsdl.Part;
import javax.wsdl.WSDLException;
import javax.wsdl.factory.WSDLFactory;
import javax.wsdl.xml.WSDLReader;
import javax.xml.namespace.QName;
import javax.xml.soap.MessageFactory;
import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPBodyElement;
import javax.xml.soap.SOAPConstants;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPFactory;
import javax.xml.soap.SOAPFault;
import javax.xml.soap.SOAPMessage;

import com.tacitknowledge.simulator.scripting.ObjectMapperException;
import com.tacitknowledge.simulator.scripting.SimulatorPojoPopulatorImpl;
import org.apache.camel.Exchange;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tacitknowledge.simulator.Adapter;
import com.tacitknowledge.simulator.Configurable;
import com.tacitknowledge.simulator.ConfigurableException;
import com.tacitknowledge.simulator.FormatAdapterException;
import com.tacitknowledge.simulator.SimulatorPojo;
import com.tacitknowledge.simulator.StructuredSimulatorPojo;

/**
 * @author galo
 */
public class SoapAdapter extends XmlAdapter implements Adapter
{
    /**
     * WSDL URL parameter. OPTIONAL.
     */
    public static final String PARAM_WSDL_URL = "wsdlURL";

    /**
     * Default key to identify PAYLOAD Map withing SimulatorPojo's root
     */
    public static final String DEFAULT_PAYLOAD_KEY = "payload";

    /**
     * sOAP Fault key
     */
    public static final String FAULT = "fault";

    /**
     * SOAP FaultCode key
     */
    public static final String FAULT_CODE = "faultCode";

    /**
     * SOAP FaultString key
     */
    public static final String FAULT_STRING = "faultString";

    /**
     * SOAP FaultActor key
     */
    public static final String FAULT_ACTOR = "faultActor";

    /**
     * Default SOAP Fault code
     */
    public static final String FAULT_CODE_SENDER = "Sender";

    /**
     * Content Type header
     */
    public static final String CONTENT_TYPE = "Content-Type";

    /**
     * Application SOAP+XML content type
     */
    public static final String CT_APP_SOAP_XML = "application/soap+xml";

    /**
     * Logger for this class.
     */
    private static Logger logger = LoggerFactory.getLogger(SoapAdapter.class);

    /**
     * Soap Factory object
     */
    private SOAPFactory soapFactory;
    /**
     * Message factory object
     */
    private MessageFactory messageFactory;

    /**
     * Key for the root where to contain the SOAP message's payload
     */
    private String payloadKey = DEFAULT_PAYLOAD_KEY;

    /**
     * WSDL service definition.
     * Will be generated from the provided WSDL.
     */
    private Definition definition;
    /**
     *  Payload NS
     */
    private String payloadNS;
    /**
     *  Payload NSUri
     */
    private String payloadNSUri;

    /**
     * SOAP message body
     */
    private SOAPMessage soapMessage;

    /**
     * Available operations defined in the provided WSDL.
     */
    private Map<String, BindingOperation> availableOps = new HashMap<String, BindingOperation>();

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
     * @param configurable
     *
     */
    public SoapAdapter(Configurable configurable)
    {
        super(configurable, false);
    }

    /**
     * @inheritDoc
     * @param exchange Exchange message
     * @return SimulatorPojo object
     * @throws FormatAdapterException if an error occurs
     */
    @Override
    protected SimulatorPojo createSimulatorPojo(final Exchange exchange) throws
            FormatAdapterException
    {
        final String body = exchange.getIn().getBody(String.class);
        return createSimulatorPojo(body);
    }

    /**
     * Sets and/or overrides instance variables from provided parameters and validates that
     * the required parameters are present.
     *
     * @throws com.tacitknowledge.simulator.ConfigurableException
     *          If any required parameter is missing or incorrect
     */
    @Override
    public void validateParameters() throws ConfigurableException
    {
        if (configuration.getParamValue(PARAM_WSDL_URL) == null)
        {
            throw new ConfigurableException("WSDL URL parameter is required");
        }

        getWSDLDefinition();
    }


    /**
     * @param pojo The populated SimulatorPojo with the script execution results
     * @param exchange      The Camel exchange
     * @return The String representation of the input data
     * @throws FormatAdapterException If any error occurs
     *
     * @inheritDoc
     */
    @SuppressWarnings({ "rawtypes", "unchecked" })
    @Override
    protected String getString(final SimulatorPojo pojo, final Exchange exchange)
        throws FormatAdapterException
    {
        logger.debug("Attempting to generate SOAP message from SimulatorPojo:\n   ", pojo);

        // --- First, check we got a "payload"
        if (!pojo.getRoot().containsKey(DEFAULT_PAYLOAD_KEY))
        {
            throw new FormatAdapterException("Expecting a PAYLOAD key in SimulatorPojo's root.");
        }

        // --- Grab the PAYLOAD results Map
        Map<String, Map> payload = (Map<String, Map>) pojo.getRoot().get(DEFAULT_PAYLOAD_KEY);

        // --- Will need it to get the SOAPMessage as a String
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        // --- If we're OK, assemble the SOAP message
        try
        {
            soapFactory = SOAPFactory.newInstance(SOAPConstants.SOAP_1_2_PROTOCOL);
            messageFactory = MessageFactory.newInstance(SOAPConstants.SOAP_1_2_PROTOCOL);

            soapMessage = messageFactory.createMessage();
            SOAPBody soapBody = soapMessage.getSOAPBody();

            // --- First things first. Check if we got a fault string...
            Map<String, String> fault = (Map<String, String>) payload.get(FAULT);
            if (fault != null
                    && !StringUtils.isEmpty(fault.get(FAULT_STRING)))
            {
                // --- If we do, we'll return a SOAP FAULT instead of a regular payload
                addFaultToResponse(
                        fault.get(FAULT_CODE),
                        fault.get(FAULT_STRING),
                        fault.get(FAULT_ACTOR));
            }
            else
            {
                // --- Validate the result method and parameters
                if (validateOperationsAndParameters(payload))
                {
                    // --- If validation was successful,
                    // remove the fault object
                    payload.remove(FAULT);

                    // ...and add the payload's content to the SOAP body
                    for (Map.Entry<String, Map> payloadItem : payload.entrySet())
                    {
                        String methodName = payloadItem.getKey();
                        Map<String, Object> itemChildren =
                                (Map<String, Object>) payloadItem.getValue();

                        BindingOperation availableOp = availableOps.get(methodName);
                        Map<String, Part> outputParts =
                                (Map<String, Part>)
                                        availableOp.getOperation().getOutput().
                                                getMessage().getParts();

                        SOAPBodyElement elem = soapBody.addBodyElement(
                                getQName(methodName + "Response"));

                        // --- Now add to the element the response parameters
                        for (Map.Entry<String, Object> child : itemChildren.entrySet())
                        {
                            String childName = child.getKey();
                            Object childValue = child.getValue();

                            // --- Add only the response parts defined in the WSDL
                            if (outputParts.containsKey(childName))
                            {
                                if (childValue instanceof Map)
                                {
                                    elem.addChildElement(
                                            getSOAPElementFromMap(
                                                    childName,
                                                    (Map<String, Object>) childValue));
                                }
                                else if (childValue instanceof String)
                                {
                                    elem.addChildElement(
                                            getSOAPElementFromString(
                                                    childName,
                                                    childValue.toString())
                                    );
                                }
                            }
                        }
                    }
                }
            }
            soapMessage.writeTo(outputStream);
        }
        catch (SOAPException se)
        {
            String errorMsg = "Unexpected SOAP exception trying to generate SOAP message: ";
            throw new FormatAdapterException(errorMsg, se);
        }
        catch (IOException ioe)
        {
            String errorMsg = "Unexpected IO Exception trying to get SOAP message as String: ";
            throw new FormatAdapterException(errorMsg, ioe);
        }

        // --- Must set the Exchange's out headers Content-Type to "application/soap+xml"
        exchange.getOut().setHeader(CONTENT_TYPE, CT_APP_SOAP_XML);

        logger.debug("Finished generating SOAP content from SimulatorPojo");
        return outputStream.toString();
    }
    protected SimulatorPojo getSimulatorPojo(final Object object) throws ObjectMapperException
    {
        final SimulatorPojo payload = SimulatorPojoPopulatorImpl.getInstance().populateSimulatorPojoFromBean(object,
                DEFAULT_PAYLOAD_KEY);
        return payload;

    }

    /**
     *
     * @param elemName The new SOAP element's name
     * @param items Map containing the new element's children
     * @return A SOAP Element containing all its corresponding SOAPElement children
     * @throws SOAPException If any SOAP error occurs
     */
    @SuppressWarnings("unchecked")
    private SOAPElement getSOAPElementFromMap(
            final String elemName,
            final Map<String, Object> items)
        throws SOAPException
    {
        SOAPElement elem = soapFactory.createElement(getQName(elemName));

        for (Map.Entry<String, Object> item : items.entrySet())
        {
            String itemName = item.getKey();
            Object itemValue = item.getValue();

            if (item.getValue() instanceof Map)
            {
                // --- Go down
                elem.addChildElement(
                        getSOAPElementFromMap(
                                itemName,
                                (Map<String, Object>) itemValue));
            }
            else if (item.getValue() instanceof String)
            {
                elem.addChildElement(
                        getSOAPElementFromString(itemName, itemValue.toString()));
            }
        }

        return elem;
    }

    /**
     *
     * @param elemName The new SOAP element's name
     * @param text The text that corresponds to the inner TextElement
     * @return A SOAPElement containing a TextElement only
     * @throws SOAPException If any SOAP error occurs
     */
    private SOAPElement getSOAPElementFromString(final String elemName, final String text)
        throws SOAPException
    {
        SOAPElement elem = soapFactory.createElement(getQName(elemName));
        elem.addTextNode(text);

        return elem;
    }

    /**
     *
     * @param soapString The String representation of the SOAP message
     * @return The generated SimulatorPojo
     * @throws FormatAdapterException If any error during the process occurs
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
    private SimulatorPojo createSimulatorPojo(final String soapString)
        throws FormatAdapterException
    {
        logger.debug("Attempting to generate SimulatorPojo from SOAP content:\n{}", soapString);

        SimulatorPojo pojo = new StructuredSimulatorPojo();

        try
        {
            messageFactory = MessageFactory.newInstance(SOAPConstants.SOAP_1_2_PROTOCOL);

            InputStream is = new ByteArrayInputStream(soapString.getBytes("UTF-8"));
            // TODO - SO WHAT ABOUT THE HEADERS?
            SOAPMessage message = messageFactory.createMessage(null, is);

            // --- So, now we got the SOAP message parsed.
            SOAPBody body = message.getSOAPBody();

            Map<String, Map> payload = (Map<String, Map>) getStructuredChilds(body);

            // --- Check that the passed methods/parameters are WSDL-valid
            validateOperationsAndParameters(payload);

            pojo.getRoot().put(payloadKey, addResponseParametersAndFault(payload));
        }
        catch (SOAPException se)
        {
            String errorMessage = "Unexpected SOAP exception trying to generate SimulatorPojo: ";
            throw new FormatAdapterException(errorMessage, se);
        }
        catch (UnsupportedEncodingException uee)
        {
            String errorMessage = "Unsupported encoding exception: ";
            throw new FormatAdapterException(errorMessage, uee);
        }
        catch (IOException ioe)
        {
            String errorMessage = "Unexpected IO exception: ";
            throw new FormatAdapterException(errorMessage, ioe);
        }

        logger.debug("Finished generating SimulatorPojo from SOAP content");
        return pojo;
    }

    /**
     * Downloads and reads a WSDL from the provided URI
     * @throws ConfigurableException If the WSDL file is not in the provided URI or is wrong
     */
    @SuppressWarnings("unchecked")
    private void getWSDLDefinition() throws ConfigurableException
    {
        try
        {
            WSDLFactory wsdlFactory = WSDLFactory.newInstance();
            WSDLReader wsdlReader = wsdlFactory.newWSDLReader();

            definition = wsdlReader.readWSDL(configuration.getParamValue(PARAM_WSDL_URL));
            if (definition == null)
            {
                throw new ConfigurableException(
                        "Definition element is null for WSDL URL: "
                                + configuration.getParamValue(PARAM_WSDL_URL));
            }

            String targetNS = definition.getTargetNamespace();
            Map<String, String> namespaces = definition.getNamespaces();

            for (Map.Entry<String, String> ns : namespaces.entrySet())
            {
                if (ns.getValue().equals(targetNS))
                {
                    payloadNS = ns.getKey();
                    payloadNSUri = ns.getValue();
                }
            }

            getAvailableOperations();
        }
        catch (WSDLException we)
        {
            String errorMsg = "Unexpected WSDL error: ";
            throw new ConfigurableException(errorMsg, we);
        }
    }

    /**
     * Populates the list of available operations and their parts as defined in the provided WSDL
     */
    @SuppressWarnings("unchecked")
    private void getAvailableOperations()
    {
        Map<QName, Binding> bindings = definition.getAllBindings();

        for (Map.Entry<QName, Binding> entry : bindings.entrySet())
        {
            List<BindingOperation> operations = entry.getValue().getBindingOperations();
            for (BindingOperation op : operations)
            {
                this.availableOps.put(op.getName(), op);
            }
        }
    }

    /**
     * 
     * @param payload Map containing SOAP message's payload
     * @return True if payload is valid. False otherwise.
     * @throws FormatAdapterException If any validation fails.
     * @throws SOAPException If any SOAP generation error occurs
     */
    @SuppressWarnings({ "rawtypes", "unchecked" })
    private boolean validateOperationsAndParameters(final Map<String, Map> payload)
        throws FormatAdapterException, SOAPException
    {
        try {
            validateParameters();
        } catch (ConfigurableException e) {
            throw new FormatAdapterException("could not validate. probably wsdl download issue",e);
        }
        // --- Review all Methods passed in the SOAP message
        for (Map.Entry<String, Map> operationEntry : payload.entrySet())
        {

            // --- We ignore the fault object
            if (operationEntry.getKey().equals(FAULT))
            {
                continue;
            }

            String opName = operationEntry.getKey();
            Map<String, Object> opParameters = operationEntry.getValue();
            
            if (!availableOps.containsKey(opName))
            {
                // --- If the requested Operation is no available, throw an error
                throw new FormatAdapterException(
                        "The requested service operation is not available in the provided WSDL: "
                                + opName);
            }

            BindingOperation availableOp = availableOps.get(opName);
            Map<String, Part> partsInAvailableOp =
                    availableOp.getOperation().getInput().getMessage().getParts();
            // --- If the operationEntry is output, get the parts from the output message
            if (configuration.getBound() == Configurable.BOUND_OUT)
            {
                partsInAvailableOp =
                        availableOp.getOperation().getOutput().getMessage().getParts();
            }

            // --- Now check that the passed parameters belong to the operationEntry in the proper
            // bound context
            // --- Everything in the payload Map, should be a Map in turn :
            //      {payload} >> {operationEntry} >> {part}
            for (Part partInOp : partsInAvailableOp.values())
            {
                if (!opParameters.containsKey(partInOp.getName()))
                {
                    String bound = configuration.getBound() == Configurable.BOUND_IN ? "inbound" : "outbound";
                    // --- If the defined method part is not in the message, throw an error
                    String errorMsg = "Missing required " + bound
                            + " parameter (" + partInOp.getName()
                            + ") for method " + opName + " as defined in the provided WSDL";

                    // --- If this is an outbound adapter, return a FAULT message in case of
                    // missing params/parts
                    if (configuration.getBound() == Configurable.BOUND_OUT)
                    {
                        addFaultToResponse("Sender", errorMsg, null);
                        return false;
                    }
                    throw new FormatAdapterException(errorMsg);
                }
            }
        }
        return true;
    }

    /**
     *
     * @param localPart The QName local part
     * @return A qualified name, with a NameSpace if it was defined in the WSDL
     */
    private QName getQName(final String localPart)
    {
        QName qname = new QName(localPart);
        if (this.payloadNS != null)
        {
            qname = new QName(this.payloadNSUri, payloadNS + ":" + localPart);
        }
        return qname;
    }

    /**
     * Adds the expected response parts and Fault to the payload Map representation
     * @param payload The payload containing the original SOAP message representation
     * @return Payload including response parts ans Fault
     */
    @SuppressWarnings({ "rawtypes", "unchecked" })
    private Map<String, Map> addResponseParametersAndFault(final Map<String, Map> payload)
    {
        // --- Set the response parameters to the invoked method in the payload
        for (Map.Entry<String, Map> methodEntry : payload.entrySet())
        {
            Map<String, Object> methodParams = methodEntry.getValue();

            BindingOperation availableOp = availableOps.get(methodEntry.getKey());

            // --- Get the response parameters and add them to the current method
            Map<String, Part> parts =
                    availableOp.getOperation().getOutput().getMessage().getParts();

            for (Part part : parts.values())
            {
                if (!methodParams.containsKey(part.getName()))
                {
                    methodParams.put(part.getName(), "");
                }
            }
        }

        // --- Add the fault object to the POJO
        Map<String, String> fault = new HashMap<String, String>();
        fault.put(FAULT_CODE, "");
        fault.put(FAULT_STRING, "");
        fault.put(FAULT_ACTOR, "");
        payload.put(FAULT, fault);

        return payload;
    }

    /**
     * Generates and adds the Fault SOAPBodyElement to the response SOAPMessage
     * @param code The fault code. Optional. Defaults to 'Sender"
     * @param string the fault string. Acts as fault description
     * @param actor The fault actor that caused the fault.
     * @throws SOAPException If any SOAP error occurs
     */
    private void addFaultToResponse(final String code, final String string, final String actor)
        throws SOAPException
    {
        SOAPFault fault = soapMessage.getSOAPBody().addFault();

        QName qname = new QName(SOAPConstants.URI_NS_SOAP_1_2_ENVELOPE, FAULT_CODE_SENDER);
        if (code != null && !StringUtils.isEmpty(code) && !code.equals(FAULT_CODE_SENDER))
        {
            qname = new QName(SOAPConstants.URI_NS_SOAP_1_2_ENVELOPE, code);
        }
        fault.setFaultCode(qname);

        fault.setFaultString(string);
        if (actor != null && !StringUtils.isEmpty(actor))
        {
            fault.setFaultActor(actor);
        }
    }
}
