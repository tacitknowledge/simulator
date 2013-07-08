package com.tacitknowledge.simulator.formats;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
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
import javax.xml.soap.Detail;
import javax.xml.soap.MessageFactory;
import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPBodyElement;
import javax.xml.soap.SOAPConstants;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPFactory;
import javax.xml.soap.SOAPFault;
import javax.xml.soap.SOAPMessage;

import com.tacitknowledge.simulator.*;
import org.apache.camel.Exchange;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tacitknowledge.simulator.configuration.beans.ScriptObjectsBuilder;
import com.tacitknowledge.simulator.configuration.beans.XmlObjectWrapper;

/**
 * This class is based on the logic in SoapAdapter however it is intended to consume/produce
 * doc/literal wrapped soap requests/responses as opposed to the RPC style implemented
 * in SoapAdapter. This class can handle much more complex SOAP scenarios and also has some fixes
 * for SoapAdapter.
 * 
 * TODO:
 * 1. Create a base class for other SoapAdapters.
 * Move the common logic from this class and SoapAdapter to the base class.
 * 2. Create implementations of adapters that would handle
 * RPC/literal, RPC/encoded (current implementation of SoapAdapter already handles some of the RPC messages),
 * document/literal messages.
 * 3. Change the way we bind transports to adapters. Currently it's a 1-1 relationship.
 * It would be nice to be able to just choose SOAP as the transport and then on the specific screen for the SOAP
 * transport be able to specify the format of the message. An alternative would be to create separate transports for
 * each type of SOAP message which is uglier.
 * 4. Change the UI to be able to specify whether messages follow SOAP1.1 or SOAP1.2 protocol.
 * Current implementation uses 1.1 only
 */
public class DocLiteralWrappedSoapAdapter extends XmlAdapter implements Adapter
{
    /**
     * WSDL URL parameter. OPTIONAL.
     */
    public static final String PARAM_WSDL_URL = "wdslURL";

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
     * Fault detail element containing the exception
     */
	private static final String FAULT_DETAIL = "detail";

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
    private static Logger logger = LoggerFactory.getLogger(DocLiteralWrappedSoapAdapter.class);

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
    public DocLiteralWrappedSoapAdapter()
    {
        super(false);
    }

    public DocLiteralWrappedSoapAdapter(Configurable configurable) {
        super(configurable, false);
    }

    public DocLiteralWrappedSoapAdapter(Configurable configurable, boolean useFullyQualifiedNodeNames) {
        super(configurable, useFullyQualifiedNodeNames);
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
        return createSimulatorPojo(exchange.getIn().getBody(String.class));
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
        Map<String, Map> payload = null;

        // --- First, check we got a "payload" in POJO's root.
        if (pojo.getRoot().containsKey(DEFAULT_PAYLOAD_KEY))
        {
        	// --- Grab the PAYLOAD results Map
        	payload = (Map<String, Map>) pojo.getRoot().get(DEFAULT_PAYLOAD_KEY);
        }
        else
        {
        	//because returning payload; in the scenario is the most common case (is there an alternative?)
        	//this branch will probably always execute and the root of the pojo will already be the content of payload.
        	// --- Grab the PAYLOAD results Map
        	payload = (Map<String, Map>) (Object) pojo.getRoot();
        }
        
        // --- Will need it to get the SOAPMessage as a String
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        // --- If we're OK, assemble the SOAP message
        try
        {
            soapFactory = SOAPFactory.newInstance(SOAPConstants.SOAP_1_1_PROTOCOL);
            messageFactory = MessageFactory.newInstance(SOAPConstants.SOAP_1_1_PROTOCOL);

            soapMessage = messageFactory.createMessage();
            SOAPBody soapBody = soapMessage.getSOAPBody();

            // --- First things first. Check if we got a fault string...
            Map<String, Object> fault = (Map<String, Object>) payload.get(FAULT);
            if (fault != null
                    && !StringUtils.isEmpty((String) fault.get(FAULT_STRING)))
            {
                // --- If we do, we'll return a SOAP FAULT instead of a regular payload
                addFaultToResponse(
                		(String) fault.get(FAULT_CODE),
                		(String) fault.get(FAULT_STRING),
                		(String) fault.get(FAULT_ACTOR),
                		(XmlObjectWrapper) fault.get(FAULT_DETAIL));
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
                                getQName(null, methodName + "Response", null));

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
                                    Map<String, Map<String, Object>> operationNameMap = (Map<String, Map<String, Object>>) childValue;
                                	//treat the case when the operation is a void
                                    if (!operationNameMap.isEmpty())
                                	{
                                        //we'd like to skip the generation of the output part name from the response
                                    	addChildElement(elem, getSOAPElements(
                                        		operationNameMap.keySet().iterator().next(),
                                        		operationNameMap.values().iterator().next()));
                                	}
                                }
                                else if (childValue instanceof String)
                                {
                                    addChildElement(elem, getSOAPElements(childName, childValue.toString()));
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

    /**
     *
     * @param elemName The new SOAP element's name
     * @param objectValue The value of element's children
     * @return A list of SOAP Elements containing all its corresponding SOAPElement children
     * @throws SOAPException If any SOAP error occurs
     */
    private List<SOAPElement> getSOAPElements(final String elemName, final Object objectValue)
   		throws SOAPException
    {
   		return getSOAPElements(elemName, null, objectValue);
    }

    /**
    *
    * @param elemName The new SOAP element's name
    * @param namespace namespace of the elements being created
    * @param objectValue The value of element's children
    * @return A list of SOAP Elements containing all its corresponding SOAPElement children
    * @throws SOAPException If any SOAP error occurs
    */
    private List<SOAPElement> getSOAPElements(final String elemName, final String namespace, final Object objectValue)
    	throws SOAPException
    {
    	return getSOAPElements(elemName, namespace, null, objectValue);
    }

   /**
     *
     * @param elemName The new SOAP element's name
     * @param namespace namespace of the elements being created
     * @param objectValue The value of element's children
     * @return A list of SOAP Elements containing all its corresponding SOAPElement children
     * @throws SOAPException If any SOAP error occurs
     */
    @SuppressWarnings({ "rawtypes", "unchecked" })
    private List<SOAPElement> getSOAPElements(final String elemName, final String namespace,
    		final String prefix, final Object objectValue)
  		throws SOAPException
    {
    	List<SOAPElement> elements = null;
    	if (objectValue instanceof List)
    	{
    		elements = new ArrayList<SOAPElement>();
    		for (Object listElemet : (List) objectValue)
    		{
    			//if the value is a list, build a soap element from every element and combine them
    			elements.addAll(getSOAPElements(
        						elemName,
        						namespace,
        						prefix,
        						listElemet));
			}
    	}
    	else if (objectValue instanceof XmlObjectWrapper)
    	{
        	XmlObjectWrapper objectWrapper = (XmlObjectWrapper) objectValue;
            // --- unwrap and go down
        	elements = getSOAPElements(
            				elemName,
                            objectWrapper.getNamespace(),
                            objectWrapper.getPrefix(),
                            objectWrapper.getValue());
    	}
    	else if (objectValue instanceof Map)
    	{
            // --- Go down
    		elements = Arrays.asList(getSOAPElementFromMap(
                    				elemName,
                    				namespace,
                    				prefix,
                    				(Map<String, Object>) objectValue));
        }
    	else
    	{
    		elements = Arrays.asList(getSOAPElementFromString(elemName, namespace, prefix, objectValue.toString()));
    	}
    	return elements;
    }

    /**
     *
     * @param elemName The new SOAP element's name
     * @param namespace namespace of the elements being created
     * @param items Map containing the new element's children
     * @return A SOAP Element containing all its corresponding SOAPElement children
     * @throws SOAPException If any SOAP error occurs
     */
    private SOAPElement getSOAPElementFromMap(
            final String elemName,
            final String namespace,
            final String prefix,
            final Map<String, Object> items)
        throws SOAPException
    {
    	SOAPElement elem = soapFactory.createElement(getQName(namespace, elemName, prefix));
    	addNamespaceAsAttribute(elem, namespace, prefix);

        for (Map.Entry<String, Object> item : items.entrySet())
        {
            String itemName = item.getKey();
            Object itemValue = item.getValue();
            addChildElement(elem, getSOAPElements(itemName, namespace, prefix, itemValue));
        }

        return elem;
    }

	/**
    *
    * @param elemName The new SOAP element's name
    * @param namespace namespace of the element being created
    * @param text The text that corresponds to the inner TextElement
    * @return A SOAPElement containing a TextElement only
    * @throws SOAPException If any SOAP error occurs
    */
   private SOAPElement getSOAPElementFromString(final String elemName, final String namespace,
		   final String prefix, final String text)
       throws SOAPException
   {
       SOAPElement elem = soapFactory.createElement(getQName(namespace, elemName, prefix));
       addNamespaceAsAttribute(elem, namespace, prefix);
       elem.addTextNode(text);

       return elem;
   }

   /**
    * Adds the namespace of the element as an attribute.
    * this is to fix a bug in the org.jboss.wsf.common.DOMWriter class in JBoss
	* that incorrectly outputs namespaces if they are not attributes
    * @param elem element to add atribute to
    * @param namespace element namespace
    * @param prefix namespace prefix
    */
   private void addNamespaceAsAttribute(SOAPElement elem, final String namespace, final String prefix)
   {
	   if (!StringUtils.isEmpty(namespace))
	   {
		   String attributeNamespace = "xmlns";
		   if (!StringUtils.isEmpty(prefix))
		   {
			   attributeNamespace = attributeNamespace + ":" + prefix;
		   }
		   elem.setAttribute(attributeNamespace, namespace);
	   }
   }
   
   /**
    * Adds every element from the list as a child of the parent element
    * @param elem parent element
    * @param soapElements a list of children elements to be added
    * @throws SOAPException If any SOAP error occurs
    */
   private void addChildElement(SOAPElement elem, List<SOAPElement> soapElements) throws SOAPException
   {
	   for (SOAPElement soapElement : soapElements)
	   {
		   elem.addChildElement(soapElement);
	   }
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
            messageFactory = MessageFactory.newInstance(SOAPConstants.SOAP_1_1_PROTOCOL);

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
        Map<QName, Binding> bindings = definition.getBindings();

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
    @SuppressWarnings("rawtypes")
    private boolean validateOperationsAndParameters(final Map<String, Map> payload)
        throws FormatAdapterException, SOAPException
    {
        // --- Review all Methods passed in the SOAP message
        for (Map.Entry<String, Map> operationEntry : payload.entrySet())
        {
            // --- We ignore the fault object
            if (operationEntry.getKey().equals(FAULT))
            {
                continue;
            }

            String opName = operationEntry.getKey();
            
            if (!availableOps.containsKey(opName))
            {
                // --- If the requested Operation is no available, throw an error
                throw new FormatAdapterException(
                        "The requested service operation is not available in the provided WSDL: "
                                + opName);
            }
        }
        return true;
    }

    /**
     *
     * @param localPart The QName local part
     * @param namespace namespace of the element being created
     * @return A qualified name, with a NameSpace if it was defined in the WSDL
     */
    private QName getQName(final String namespace, final String localPart, final String prefix)
    {
    	if (!StringUtils.isEmpty(namespace))
    	{
    		String safePrefix = "";
    		if (!StringUtils.isEmpty(prefix))
    		{
    			safePrefix = prefix;
    		}
    		return new QName(namespace, localPart, safePrefix);
    	}
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
                    methodParams.put(part.getName(), new HashMap());
                }
            }
        }

        // --- Add the fault object to the POJO
        Map<String, Object> fault = new HashMap<String, Object>();
        fault.put(FAULT_CODE, "");
        fault.put(FAULT_STRING, "");
        fault.put(FAULT_ACTOR, "");
        fault.put(FAULT_DETAIL, new XmlObjectWrapper());
        payload.put(FAULT, fault);

        return payload;
    }

    /**
     * Generates and adds the Fault SOAPBodyElement to the response SOAPMessage
     * @param code The fault code. Optional. Defaults to 'Sender"
     * @param string the fault string. Acts as fault description
     * @param actor The fault actor that caused the fault.
     * @param detailWrapper object containing information about fault detail
     * @throws SOAPException If any SOAP error occurs
     */
    private void addFaultToResponse(final String code, final String string, final String actor,
    		final XmlObjectWrapper detailWrapper)
        throws SOAPException
    {
        SOAPFault fault = soapMessage.getSOAPBody().addFault();

        QName qname = new QName(SOAPConstants.URI_NS_SOAP_1_1_ENVELOPE, FAULT_CODE_SENDER);
        if (code != null && !StringUtils.isEmpty(code) && !code.equals(FAULT_CODE_SENDER))
        {
            qname = new QName(SOAPConstants.URI_NS_SOAP_1_1_ENVELOPE, code);
        }
        fault.setFaultCode(qname);

        fault.setFaultString(string);
        if (actor != null && !StringUtils.isEmpty(actor))
        {
            fault.setFaultActor(actor);
        }
        if (detailWrapper.getValue() != null)
        {
        	Detail detail = fault.addDetail();
        	detail.addChildElement(
        			getSOAPElementFromString((String) detailWrapper.getValue(), detailWrapper.getNamespace(), null, ""));
        }
    }
    
    @Override
    protected Map<String, Object> generateClasses(SimulatorPojo pojo)
    		throws FormatAdapterException
    {
    	pojo.getRoot().put("objectBuilder", new ScriptObjectsBuilder());
    	return pojo.getRoot();
    }
}
