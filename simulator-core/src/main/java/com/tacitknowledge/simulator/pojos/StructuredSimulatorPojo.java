package com.tacitknowledge.simulator.pojos;

import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

/**
 * Created by IntelliJ IDEA.
 * User: galo
 * Date: Nov 3, 2009
 * Time: 9:57:17 AM
 * To change this template use File | Settings | File Templates.
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
