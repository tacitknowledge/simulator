package com.tacitknowledge.simulator.formats;

import com.tacitknowledge.simulator.Adapter;
import com.tacitknowledge.simulator.SimulatorPojo;
import com.tacitknowledge.simulator.StructuredSimulatorPojo;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.Text;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.io.StringReader;
import java.util.HashMap;
import java.util.Map;
import java.util.ArrayList;
import java.util.List;

/**
 * Implementation of the Adapter interface for the XML format
 *
 * @author Jorge Galindo (jgalindo@tacitknowledge.com)
 */
public class XmlAdapter implements Adapter<Object>
{
    /**
     * Logger for this class.
     */
    private static Logger logger = Logger.getLogger(XmlAdapter.class);

    /**
     * Adapts the String data received from the inbound transport into XML format.
     *
     * @return an object constructed based on the inbound transport data.
     * @param object the incoming data object to adapt to XML format.
     * @throws FormatAdapterException in case the incoming message is not formatted correctly
     * for XmlAdapter to adapt it.
     */
    public SimulatorPojo adaptFrom(Object object) throws FormatAdapterException
    {
        if (!(object instanceof String))
        {
            logger.error("Incoming data object, wasn't correctly formatted for the XmlParser");

            throw new FormatAdapterException(
                    "Input data is expected to be a String. Instead, input data is "
                            + object.getClass().getName());
        }

        SimulatorPojo pojo = new StructuredSimulatorPojo();

        try
        {
            // --- First, parse the XML string into a document
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            InputSource is = new InputSource();

            is.setCharacterStream(new StringReader((String) object));

            Document doc = db.parse(is);

            // --- Well-formatted Xml should have a single root, right?
            Element docElem = doc.getDocumentElement();
            Map<String, Map> root = new HashMap<String, Map>();

            logger.debug("Constructing the root node of the xml document");

            root.put(docElem.getTagName(), getStructuredChilds(docElem));

            pojo.setRoot(root);
        }

        catch (ParserConfigurationException pce)
        {
            String errorMessage = "Unexpected error trying to adapt from Xml: " + pce.getMessage();

            logger.error(errorMessage, pce);

            throw new FormatAdapterException(errorMessage, pce);
        }

        catch (SAXException se)
        {
            String errorMessage = "Unexpected error trying to adapt from Xml: " + se.getMessage();

            logger.error(errorMessage, se);

            throw new FormatAdapterException(errorMessage, se);
        }
        catch (IOException ioe)
        {
            String errorMessage = "Unexpected error trying to adapt from Xml: " + ioe.getMessage();

            logger.error(errorMessage, ioe);

            throw new FormatAdapterException(errorMessage, ioe);
        }

        return pojo;
    }

    /**
     * Constructs all of the child nodes of the XML document in a Map of Name, Value structure.
     * @param elem XML element to parse for children.
     * @return Map of Name, Value structure.
     */
    private Map getStructuredChilds(Element elem)
    {
        Map<String, Object> structuredChild = new HashMap<String, Object>();

        // --- Get first child
        Node nd = elem.getFirstChild();
        // --- Iterate throu all the elem childs
        while (nd != null)
        {
            if (!(nd instanceof Element))
            {
                // --- If nd is not an Element, we skip it and got to the next child
                nd = nd.getNextSibling();
                continue;
            }

            // --- We make sure we get an Element so we can get the underlying node name
            Element child = (Element) nd;
            String currNodeName = child.getTagName();

            logger.debug("Constructing the " + currNodeName + " node of the xml document");

            // --- Check if the structuredChilds Map contains the current node name
            if (structuredChild.containsKey(currNodeName))
            {
                // --- Get the original attribute,
                // if this attribute name is already registered, it means this should be a List
                Object tmp = structuredChild.get(currNodeName);
                // --- Check if it's already a List
                List currList;
                if (tmp instanceof List)
                {
                    // --- If it is, just keep the reference
                    currList = (List) tmp;
                }
                else
                {
                    // --- If it isn't, create a new list...
                    currList = new ArrayList();
                    // --- ...insert the previous attribute value to the list
                    currList.add(tmp);
                    // --- ...and place the list into its corresponding attribute name
                    structuredChild.put(currNodeName, currList);
                }
                // --- Add the current node as a structured child
                currList.add(getStructuredChilds(child));
            }
            else
            {
                // --- If the child is a text node with value. return null
                if (child instanceof Text && !child.getNodeValue().trim().equals(""))
                {
                    return null;
                }
                else
                {
                    // ...otherwise, go down the child structure
                    Map childValue = getStructuredChilds(child);
                    if (childValue == null || childValue.isEmpty())
                    {
                        structuredChild.put(currNodeName, child.getFirstChild().getNodeValue());
                    }
                    else
                    {
                        structuredChild.put(currNodeName, getStructuredChilds(child));
                    }
                }

            }

            nd = child.getNextSibling();
        }

        return structuredChild;
    }

    /**
     * Adapts the data from simulation to the XML formatted object
     *
     * @param pojo the SimulatorPojo with the data to be transformed into XML structure.
     * @return an object constructed based on the data received from execution of the simulation
     */
    public Object adaptTo(SimulatorPojo pojo)
    {
        return null;
    }
}
