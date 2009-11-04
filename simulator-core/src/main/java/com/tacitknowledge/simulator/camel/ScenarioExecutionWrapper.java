package com.tacitknowledge.simulator.camel;

import com.tacitknowledge.simulator.ConversationScenario;
import com.tacitknowledge.simulator.SimulatorPojo;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;

import java.util.List;


/**
 * @author nikitabelenkiy
 */
public class ScenarioExecutionWrapper implements Processor
{

    private List<ConversationScenario> scenarios;


    public ScenarioExecutionWrapper(List<ConversationScenario> scenarios)
    {
        this.scenarios = scenarios;
    }

    /**
     * @param exchange exchange.getIn().getBody() contains data from inbound adapter
     * @throws Exception
     */
    public void process(Exchange exchange) throws Exception
    {

        Object data = exchange.getIn().getBody();
        Object result = null;
        //here we are looking for first matching scenario and ignore all other scenarios
        for (ConversationScenario scenario : scenarios)
        {
            if (scenario.isActive() && scenario.matchesCondition(data))
            {
                result = scenario.run((SimulatorPojo) data);
                //setting the result of simulation as imput parameter for the next step
                exchange.getIn().setBody(result);
                break;
            }
        }
    }
}
