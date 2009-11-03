package com.tacitknowledge.simulator.adapters;

import com.tacitknowledge.simulator.FormatConstants;

import java.util.Map;
import java.util.HashMap;

/**
 * @author galo
 */
public class AdapterFactory {
    private static Map<String, Adapter> adapters = new HashMap<String, Adapter>(){{
        put(FormatConstants.JSON, new JsonAdapter());
        put(FormatConstants.XML, new XmlAdapter());
    }};
    public static Adapter getAdapter(String inboundFormat) {
        return adapters.get(inboundFormat);
    }
}
