package com.tacitknowledge.simulator.camel;

import com.tacitknowledge.simulator.ConversationScenario;
import com.tacitknowledge.simulator.SimulatorPojo;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

/**
 * Wrapper for the scenario execution.
 *
 * @author Nikita Belenkiy (nbelenkiy@tacitknowledge.com)
 * @author Alexandru Dereveanco (adereveanco@tacitknowledge.com)
 */
public class ScenarioExecutionWrapper implements Processor
{
    /**
     * Container of the scenarios to run
     */
    private List<ConversationScenario> scenarios;

    /**
     * Logger for the ScenarioExecutionWrapper class.
     */
    private static Logger logger
            = Logger.getLogger(ScenarioExecutionWrapper.class);

    /**
     * Constructor for the ScenarioExecutionWrapper.
     *
     * @param scenarios list of the scenarios to run.
     */
    public ScenarioExecutionWrapper(List<ConversationScenario> scenarios)
    {
        if (scenarios == null)
        {
            logger.warn("Something is probably wrong: Scenarios are null");
            this.scenarios = new ArrayList<ConversationScenario>();
        }
        else
        {
            this.scenarios = scenarios;
        }
    }

    /**
     * Processes the exchange object received from the previous step of the route. Iterates through
     * all of the provided scenarios and return the processed result of the first matched scenario.
     *
     * @param exchange exchange.getIn().getBody() contains data from inbound adapter
     * @throws Exception in case of error.
     */
    public void process(Exchange exchange) throws Exception
    {
        Object data = exchange.getIn().getBody();
        Object result = null;
        //TODO add case when matching scenario is not found
        // here we are looking for first matching scenario and ignore all other scenarios
        for (ConversationScenario scenario : scenarios)
        {
            logger.debug("Evaluating scenario : " + scenario.toString());

            if (scenario.isActive() && scenario.matchesCondition(data))
            {
                logger.debug("Scenario : " + scenario
                        + " was matched, executing the transformation script.");

                result = scenario.executeTransformation((SimulatorPojo) data);
                // setting the result of simulation as imput parameter for the next step
                exchange.getIn().setBody(result);
                break;
            }
        }
    }
}
