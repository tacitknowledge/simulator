package com.tacitknowledge.simulator.transports;

import com.tacitknowledge.simulator.Configurable;
import com.tacitknowledge.simulator.ConfigurableException;
import com.tacitknowledge.simulator.Transport;

import java.util.List;
import java.util.Map;

/**
 * Date: 30.11.2009
 * Time: 15:46:54
 *
 * @author Nikita Belenkiy (nbelenkiy@tacitknowledge.com)
 */
public class MockOutTransport extends BaseTransport implements Transport
{
    /**
     * Default Constructor
     */
    public MockOutTransport()
    {
        super(Configurable.BOUND_OUT, "mockOutTransport", null);
    }

    /**
     * Returns the uri in string format
     * @return uri representation
     */
    public String toUriString()
    {
        return "mock:result";
    }

    /**
     * List of parameters for this transport
     * @return List of parameters
     */
    public List<List> getParametersList()
    {
        return null;
    }

    /**
     * Validate that all the required parameters have been provided.
     *
     * @throws com.tacitknowledge.simulator.ConfigurableException
     *          If any required parameter has not been set.
     */
    @Override
    protected void validateParameters() throws ConfigurableException
    {

    }
}
