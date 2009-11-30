package com.tacitknowledge.simulator.transports;

import com.tacitknowledge.simulator.Transport;

import java.util.List;
import java.util.Map;

/**
* Date: 30.11.2009
* Time: 15:46:40
*
* @author Nikita Belenkiy (nbelenkiy@tacitknowledge.com)
*/
public class MockInTransport implements Transport {
    public String getType()
    {
        return "Mock In Transport";
    }

    public String toUriString()
    {
        return "direct:start";
    }

    public List<List> getParametersList()
    {
        return null;
    }

    public void setParameters(Map<String, String> parameters)
    {

    }
}
