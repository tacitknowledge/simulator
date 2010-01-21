package com.tacitknowledge.simulator.transports;

import com.tacitknowledge.simulator.Configurable;
import com.tacitknowledge.simulator.ConfigurableException;
import com.tacitknowledge.simulator.Transport;

import java.util.List;
import java.util.Map;

/**
 * Date: 30.11.2009
 * Time: 15:46:40
 *
 * @author Nikita Belenkiy (nbelenkiy@tacitknowledge.com)
 */
public class MockInTransport extends BaseTransport implements Transport
{
    /**
     * Default Constructor
     */
    public MockInTransport()
    {
        super("mockInTransport");
    }

    /**
     * Get transport type
     * @return - String type
     */
    public String getType()
    {
        return "Mock In Transport";
    }

    /**
     * Default url route for this transport
     * @return uri string
     */
    public String toUriString()
    {
        return "direct:start";
    }

    /**
     * Retrieves parameter list
     * @return - List of parameters
     */
    public List<List> getParametersList()
    {
        return null;
    }

    /**
     * Set parameters for this transport
     * @param parameters Configurable parameter values
     */
    public void setParameters(final Map<String, String> parameters)
    {

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
