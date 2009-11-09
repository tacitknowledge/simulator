package com.tacitknowledge.simulator.formats;

import com.tacitknowledge.simulator.Adapter;
import com.tacitknowledge.simulator.SimulatorPojo;
import com.tacitknowledge.simulator.StructuredSimulatorPojo;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;

import org.xml.sax.InputSource;
import org.w3c.dom.*;

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
     * Adapts the String data received from the inbound transport into XML format.
     *
     * @return an object constructed based on the inboud transport data.
     */
    public SimulatorPojo adaptFrom(Object o) throws FormatAdapterException
    {
        if (!(o instanceof String))
            throw new FormatAdapterException("Input data is expected to be a String. Instead, input data is " + o.getClass().getName());

        SimulatorPojo pojo = new StructuredSimulatorPojo();

        try
        {
            // --- First, parse the XML string into a document
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            InputSource is = new InputSource();
            is.setCharacterStream(new StringReader((String) o));

            Document doc = db.parse(is);

            // --- Well-formatted Xml should have a single root, right?
            Element docElem = doc.getDocumentElement();
            Map<String, Map> root = new HashMap<String, Map>();
            root.put(docElem.getTagName(), getStructuredChilds(docElem));

            pojo.setRoot(root);
        }
        catch (Exception e)
        {
            throw new FormatAdapterException("Unexpected error trying to adapt from Xml: " + e.getMessage(), e);
        }

        return pojo;
    }

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

            nd = child.getNextSibling();
        }

        return structuredChild;
    }

    /**
     * Adapts the data from simulation to the XML formatted object
     *
     * @return an object constructed based on the data received from execution of the simulation
     */
    public Object adaptTo(SimulatorPojo pojo)
    {
        return null;
    }
}
