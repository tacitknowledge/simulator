package com.tacitknowledge.simulator;

import java.util.Map;

/**
 * Definition for the Simulator data transport object.
 * @author Jorge Galindo (jgalindo@tacitknowledge.com)
 */
public interface SimulatorPojo
{
    /**
     * Getter for the root node of the pojo
     * @return the root node
     */
    Map getRoot();

    /**
     * Setter for the root node of the pojo
     * @param root root node
     */
    void setRoot(Map root);

    /**
     * Gets the attribute value for the provided name
     * @param name name of the attribute
     * @return the value of tha attribute
     */
    Object getAttribute(String name);
}
