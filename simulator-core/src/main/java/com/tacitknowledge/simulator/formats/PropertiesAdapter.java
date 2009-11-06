package com.tacitknowledge.simulator.formats;

import com.tacitknowledge.simulator.Adapter;
import com.tacitknowledge.simulator.SimulatorPojo;

/**
 * Implementation of the Adapter interface for the Properties format
 *
 * @author Jorge Galindo (jgalindo@tacitknowledge.com)
 */
public class PropertiesAdapter implements Adapter<Object>
{
    /**
     * Adapts the data received from the inbound transport into CSV format.
     *
     * @param object object the incoming data object to adapt to CSV format.
     * @return an object constructed based on the inboud transport data.
     */
    public SimulatorPojo adaptFrom(Object object)
    {
        return null;
    }

    /**
     * Adapts the data from simulation to the Properties formatted object
     *
     * @param pojo simulator pojo with data to be adapted to Properties format
     * @return an object constructed based on the data received from execution of the simulation
     */
    public Object adaptTo(SimulatorPojo pojo)
    {
        return null;
    }
}
