package com.tacitknowledge.simulator;

import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

/**
 * @author Jorge Galindo (jgalindo@tacitknowledge.com)
 */
public class StructuredSimulatorPojo implements SimulatorPojo
{
    /**
     * The Map containing the actual input data, structured depending on the
     * original input format.
     * The Map's contents can be other containing objects (either Map or List) or String attributes
     */
    private Map root;

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

        // --- 
        StringTokenizer st = new StringTokenizer(name, ".");
        while (st.hasMoreTokens())
        {
            String token = st.nextToken();
            Object tmpObj = root;

            if (tmpObj instanceof Map)
            {

            }

            /*
            if (tmpMap.containsKey(token)) {
                if (root.get(name) instanceof Map && st.hasMoreTokens()) {
                    tmpMap = (Map) root.get(token);
                } else {
                    attr = (String) root.get(token);
                }
            }*/
        }
        return attr;
    }
}
