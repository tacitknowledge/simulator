package com.tacitknowledge.simulator;

import com.tacitknowledge.simulator.formats.JsonAdapter;
import com.tacitknowledge.simulator.formats.XmlAdapter;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Jorge Galindo (jgalindo@tacitknowledge.com)
 */
class AdapterFactory
{
    private static Map<String, Adapter> adapters = new HashMap<String, Adapter>()
    {{
            put(FormatConstants.JSON, new JsonAdapter());
            put(FormatConstants.XML, new XmlAdapter());
        }};

    public static Adapter getAdapter(String inboundFormat)
    {
        return adapters.get(inboundFormat);
    }
}
