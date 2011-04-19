package com.tacitknowledge.simulator;

import java.util.HashMap;
import java.util.Map;


/**
 * The data transfer object used in simulations.
 *
 * @author Jorge Galindo (jgalindo@tacitknowledge.com)
 */
public class StructuredSimulatorPojo implements SimulatorPojo
{
    /**
     * The Map containing the actual input data, structured depending on the
     * original input format. Each Adapter implementation should
     * structure data as fitting to its format.
     * The Map's contents can be other containing objects (either Map or List) or String attributes
     */
    private Map<String, Object> root = new HashMap<String, Object>();

    /**
     * {@inheritDoc}
     */
    public Map<String, Object> getRoot()
    {
        return root;
    }

    @Override
    public String toString()
    {
        StringBuilder sb = new StringBuilder();
        
        for (Map.Entry<String, Object> entry : root.entrySet())
        {
            sb.append("{");
            sb.append(entry.getKey());
            sb.append(": ");
            sb.append(entry.getValue());
            sb.append("}");
        }
       
        return sb.toString();
    }
}
