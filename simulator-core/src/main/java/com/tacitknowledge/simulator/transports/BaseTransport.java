package com.tacitknowledge.simulator.transports;

/**
 * Base Transport class. Contains attributes common to all transport implementations
 *
 * @author Jorge Galindo (jgalindo@tacitknowledge.com)
 */
public class BaseTransport
{
    /**
     * Transport type
     */
    private String type;

    /**
     * Getter for @see #type
     * @return @see #type
     */
    public String getType()
    {
        return type;
    }

    /**
     * Setter for @see #type
     * @param type @see #type
     */
    public void setType(String type)
    {
        this.type = type;
    }
}
