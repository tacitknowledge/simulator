package com.tacitknowledge.simulator.formats;

import com.tacitknowledge.simulator.Adapter;
import com.tacitknowledge.simulator.FormatAdapterException;
import com.tacitknowledge.simulator.SimulatorPojo;
import com.tacitknowledge.simulator.StructuredSimulatorPojo;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
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
public class XmlAdapter implements Adapter
{
    /**
     * Logger for this class.
     */
    private static Logger logger = Logger.getLogger(XmlAdapter.class);
    /**
     * {@inheritDoc}
     */
    public SimulatorPojo adaptFrom(Object o) throws FormatAdapterException
    {
        if (!(o instanceof String))
        {
            throw new FormatAdapterException("Input data is expected to be a String. Instead, "
                    + "input data is " + o.getClass().getName());
        }
        SimulatorPojo pojo = new StructuredSimulatorPojo();

        try
        {
            // --- First, parse the XML string into a document
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db;

            db = dbf.newDocumentBuilder();
            InputSource is = new InputSource();
            is.setCharacterStream(new StringReader((String) o));

            Document doc = db.parse(is);

            // --- Well-formatted Xml should have a single root, right?
            Element docElem = doc.getDocumentElement();
            Map<String,Object> root = new HashMap<String, Object>();
            root.put(docElem.getTagName(), getStructuredChilds(docElem));

            pojo.setRoot(root);
        }
        catch (ParserConfigurationException e)
        {
            String errorMessage = "Unexpected parser configuration exception";
            logger.error(errorMessage, e);
            throw new FormatAdapterException(errorMessage, e);
        }
        catch (SAXException e)
        {
            String errorMessage = "Unexpected SAX exception";
            logger.error(errorMessage, e);
            throw new FormatAdapterException(errorMessage, e);
        }
        catch (IOException e)
        {
            String errorMessage = "Unexpected IO exception";
            logger.error(errorMessage, e);
            throw new FormatAdapterException(errorMessage, e);
        }

        return pojo;
    }

    /**
     * {@inheritDoc}
     */
    public Object adaptTo(SimulatorPojo pojo)
    {
        return null;
    }

    /**
     * Returns a structured representation of the XML element on a Map,
     * in which container nodes are represented as
     * Map value, duplicated-name nodes as List value and the leaf nodes as String values
     * @param elem the element to structure the child nodes for
     * @return a Map of child nodes of the XML document
     */
    private Map getStructuredChilds(Element elem)
    {
        // --- The Map to be returned
        Map<String, Object> structuredChild = new HashMap<String, Object>();

        // --- Get first child
        Node nd = elem.getFirstChild();
        // --- Iterate throu all the elem childs
        while (nd != null)
        {
            if (!(nd instanceof Element))
            {
                // --- If nd is not an Element, we skip it and go to the next child
                nd = nd.getNextSibling();
                continue;
            }

            // --- We make sure we get an Element so we can get the underlying node name
            Element child = (Element) nd;
            String currNodeName = child.getTagName();

            // --- Check if the structuredChild Map contains the current node name
            if (structuredChild.containsKey(currNodeName))
            {
                // --- Get the original attribute,
                // if this attribute name is already registered, it means this should be a List
                Object tmp = structuredChild.get(currNodeName);
                // --- Check if it's already a List
                List<Object> currList;
                if (tmp instanceof List)
                {
                    // --- If it is, just keep the reference
                    currList = (List<Object>) tmp;
                }
                else
                {
                    // --- If it isn't, create a new list...
                    currList = new ArrayList<Object>();
                    // --- ...insert the previous attribute value to the list
                    currList.add(tmp);
                    // --- ...and place the list into its corresponding attribute name
                    structuredChild.put(currNodeName, currList);
                }

                Map childValue = getStructuredChilds(child);
                // --- If there was only one text child, use its value
                if (childValue == null || childValue.isEmpty())
                {
                    // --- Add the String value
                    currList.add(child.getFirstChild().getNodeValue());
                }
                else
                {
                    // --- Add the current node as a structured child
                    currList.add(getStructuredChilds(child));
                }
            }
            else
            {
                // --- If the currNodeName hasn't been registered, try to get its structured childs
                Map childValue = getStructuredChilds(child);
                if (childValue == null || childValue.isEmpty())
                {
                    // --- If the childValue is null or empty, means the
                    // underlying child is a Text node.
                    // Just assign the child's text value as it is.
                    structuredChild.put(currNodeName, child.getFirstChild().getNodeValue());
                }
                else
                {
                    // --- otherwise, assign the obtained structured Map
                    structuredChild.put(currNodeName, childValue);
                }
            }

            nd = child.getNextSibling();
        }

        return structuredChild;
    }
}
