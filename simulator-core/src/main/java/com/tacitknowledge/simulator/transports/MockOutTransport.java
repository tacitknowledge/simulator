package com.tacitknowledge.simulator.transports;

import com.tacitknowledge.simulator.Transport;

import java.util.List;
import java.util.Map;

/**
 * Date: 30.11.2009
 * Time: 15:46:54
 *
 * @author Nikita Belenkiy (nbelenkiy@tacitknowledge.com)
 */
public class MockOutTransport implements Transport
{
    public String getType()
    {
        return "Mock Out Transport";
    }

    public String toUriString()
    {
        return "mock:result";
    }

    public List<List> getParametersList()
    {
        return null;
    }

    public void setParameters(Map<String, String> parameters)
    {

    }
}
