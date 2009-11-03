package com.tacitknowledge.simulator;

/**
 * Wrapper for transport configuration.
 * Each transport implementation should hold the required attributes for each transport type
 * (server, port, queue, path, etc)
 *
 * @author Jorge Galindo (jgalindo@tacitknowledge.com)
 */
public interface Transport
{
    /**
     * Returns the Transport type
     *
     * @return
     */
    String getType();

    /**
     * Returns a valid String URI representation of this transport for Camel route creation
     * e.g.: file://path/to/file/directory , jms:queue/myqueue ,
     *
     * @return
     */
    String toUriString();
}
