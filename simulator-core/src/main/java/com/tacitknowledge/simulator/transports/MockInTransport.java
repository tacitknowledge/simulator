package com.tacitknowledge.simulator.transports;

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
    public List<List> getParametersList()
    {
        return null;
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
