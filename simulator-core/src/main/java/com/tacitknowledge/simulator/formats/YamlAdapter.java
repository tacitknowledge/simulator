package com.tacitknowledge.simulator.formats;

import com.tacitknowledge.simulator.Adapter;
import com.tacitknowledge.simulator.SimulatorPojo;

/**
 * Implementation of the Adapter interface for the YAML format
 *
 * @author Jorge Galindo (jgalindo@tacitknowledge.com)
 */
public class YamlAdapter implements Adapter<Object>
{
    /**
     * Adapts the String data received from the inbound transport into YAML format.
     *
     * @return an object constructed based on the inbound transport data.
     * @param object the incoming data object to adapt to YAML format.
     * for YamlAdapter to adapt it.
     */
    public SimulatorPojo adaptFrom(Object object)
    {
        //TODO Implement this functionality.
        return null;
    }

    /**
     * Adapts the data from simulation to the YAML formatted object
     *
     * @param pojo the SimulatorPojo with the data to be transformed into YAML structure.
     * @return an object constructed based on the data received from execution of the simulation
     */
    public Object adaptTo(SimulatorPojo pojo)
    {
        //TODO Implement this functionality.
        return null;
    }
}
