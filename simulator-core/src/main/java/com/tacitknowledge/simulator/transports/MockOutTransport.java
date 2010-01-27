package com.tacitknowledge.simulator.transports;

import com.tacitknowledge.simulator.Configurable;
import com.tacitknowledge.simulator.ConfigurableException;
import com.tacitknowledge.simulator.Transport;

import java.util.List;

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
     * {@inheritDoc}
     */
    protected String getUriString()
    {
        return "mock:result";
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
    @Override
    protected void validateParameters() throws ConfigurableException
    {

    }
}
