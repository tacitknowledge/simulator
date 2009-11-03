package com.tacitknowledge.simulator;

import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

/**
 *
 *
 * @author galo
 */
public class StructuredSimulatorPojo implements SimulatorPojo {
    private Map root;

    public StructuredSimulatorPojo() {
        root = new HashMap();
    }

    /**
     *
     * @param name The full-path to the attribute to return. e.g.: order.shippinginfo.address1
     * @return
     */
    public String getAttribute(String name) {
        String attr = null;

        // --- 
        StringTokenizer st = new StringTokenizer(name, ".");
        while (st.hasMoreTokens()) {
            String token = st.nextToken();
            Object tmpObj = root;

            if (tmpObj instanceof Map) {

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
