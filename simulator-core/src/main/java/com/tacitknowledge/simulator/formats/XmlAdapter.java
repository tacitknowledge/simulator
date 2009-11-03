package com.tacitknowledge.simulator.formats;

import com.tacitknowledge.simulator.Adapter;
import com.tacitknowledge.simulator.SimulatorPojo;

/**
 * Implementation of the Adapter interface for the XML format
 *
 * @author Jorge Galindo (jgalindo@tacitknowledge.com)
 */
public class XmlAdapter implements Adapter
{
    /**
     * Adapts the data received from the inbound transport into XML format.
     * 
     * @return an object constructed based on the inboud transport data.
     */
    public SimulatorPojo adaptFrom(Object o)
    {
        return null;
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
