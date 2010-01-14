package com.tacitknowledge.simulator;

import java.util.Map;

/**
 * Definition for the Simulator data transport object.
 *
 * @author Jorge Galindo (jgalindo@tacitknowledge.com)
 */
public interface SimulatorPojo
{
    /**
     * Getter for the root node of the pojo
     *
     * @return the root node
     */
    Map<String, Object> getRoot();
}
