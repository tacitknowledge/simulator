package com.tacitknowledge.simulator.transports;

/**
 * Base Transport class. Contains attributes common to all transport implementations
 * @author galo
 */
public class BaseTransport
{
    /**
     * Transport type
     */
    private String type;

    /**
     * @see #type
     * @return
     */
    public String getType()
    {
        return type;
    }

    /**
     * @see #type
     * @param type
     */
    public void setType(String type)
    {
        this.type = type;
    }
}
