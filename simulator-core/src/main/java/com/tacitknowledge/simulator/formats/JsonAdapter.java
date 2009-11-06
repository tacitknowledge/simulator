package com.tacitknowledge.simulator.formats;

import com.tacitknowledge.simulator.Adapter;
import com.tacitknowledge.simulator.SimulatorPojo;

/**
 * Implementation of the Adapter interface for the JSON format
 *
 * @author Jorge Galindo (jgalindo@tacitknowledge.com)
 */
public class JsonAdapter implements Adapter<Object>
{
    /**
     * Adapts the data received from the inbound transport into JSON format.
     *
     * @param object object the incoming data object to adapt to JSON format.
     * @return an object constructed based on the inboud transport data.
     */
    public SimulatorPojo adaptFrom(Object object)
    {
        return null;
    }

    /**
     * Adapts the data from simulation to the JSON formatted object
     *
     * @param pojo simulator pojo with data to be adapted to JSON format
     * @return an object constructed based on the data received from execution of the simulation
     */
    public Object adaptTo(SimulatorPojo pojo)
    {
        return null;
    }
}
