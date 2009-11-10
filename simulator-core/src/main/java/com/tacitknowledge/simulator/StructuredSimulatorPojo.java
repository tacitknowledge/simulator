package com.tacitknowledge.simulator;

import java.util.HashMap;
import java.util.Map;


/**
 * The data transfer object used in simulations.
 * @author Jorge Galindo (jgalindo@tacitknowledge.com)
 */
public class StructuredSimulatorPojo implements SimulatorPojo
{
    /**
     * {@inheritDoc}
     */
    public Map getRoot()
    {
        return root;
    }

    /**
     * The Map containing the actual input data, structured depending on the
     * original input format. Each Adapter implementation should
     * structure data as fitting to its format.
     * The Map's contents can be other containing objects (either Map or List) or String attributes
     */
    private Map root;

    /**
     * Constructor for the StructuredSimulatorPojo
     */
    public StructuredSimulatorPojo()
    {
        root = new HashMap();
    }

    /**
     * NOT FULLY IMPLEMENTED
     * @param name The full-path to the attribute to return. e.g.: order.shippinginfo.address1
     * @return The required attribute. Most times than not, this would be a String
     */
    public Object getAttribute(String name)
    {
        String attr = null;
        //TODO Implement or remove this functionality.

//        StringTokenizer st = new StringTokenizer(name, ".");
//        while (st.hasMoreTokens())
//        {
//            String token = st.nextToken();
//            Object tmpObj = root;
//
//            if (tmpObj instanceof Map)
//            {
//
//            }
//
//
//            if (tmpMap.containsKey(token)) {
//                if (root.get(name) instanceof Map && st.hasMoreTokens()) {
//                    tmpMap = (Map) root.get(token);
//                } else {
//                    attr = (String) root.get(token);
//                }
//            }
//        }
        return attr;
    }

    /**
     * Setter for @see #root
     * @param root The structured data in a Map
     */
    public void setRoot(Map root)
    {
        this.root = root;
    }
}
