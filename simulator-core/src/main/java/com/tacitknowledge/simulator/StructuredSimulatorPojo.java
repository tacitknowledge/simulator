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
    public Map<String, Object> getRoot()
    {
        return root;
    }

    /**
     * The Map containing the actual input data, structured depending on the
     * original input format. Each Adapter implementation should
     * structure data as fitting to its format.
     * The Map's contents can be other containing objects (either Map or List) or String attributes
     */
    private Map<String, Object> root;

    /**
     * Constructor for the StructuredSimulatorPojo
     */
    public StructuredSimulatorPojo()
    {
        root = new HashMap<String, Object>();
    }

    /**
     * Setter for @see #root
     * @param root The structured data in a Map
     */
    public void setRoot(Map<String, Object> root)
    {
        this.root = root;
    }
}
