package com.tacitknowledge.simulator;

import java.util.List;
import java.util.Map;

/**
 * Transport interface for different transports.
 *
 * @author Jorge Galindo (jgalindo@tacitknowledge.com)
 */
public interface Transport extends Configurable
{
    /**
     * Returns the Transport type
     *
     * @return transport type
     */
    String getType();

    /**
     * Returns a valid String URI representation of this transport for Camel route creation e.g.:
     * file://path/to/file/directory , jms:queue/myqueue ,
     *
     * @return URI representation of the transport
     * @throws ConfigurableException If a required parameter is missing or not properly formatted.
     * @throws TransportException If any other error occurs
     */
    String toUriString() throws ConfigurableException, TransportException;
}
