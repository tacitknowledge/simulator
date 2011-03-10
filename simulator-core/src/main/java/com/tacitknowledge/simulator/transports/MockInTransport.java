package com.tacitknowledge.simulator.transports;

import java.util.Map;

import com.tacitknowledge.simulator.ConfigurableException;
import com.tacitknowledge.simulator.Transport;

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
     * {@inheritDoc}
     */
    @Override
    protected String getUriString()
    {
        return "direct:start";
    }

    /**
     * {@inheritDoc}
     */
    public void setParameters(final Map<String, String> parameters)
    {

    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void validateParameters() throws ConfigurableException
    {

    }
}
