package com.tacitknowledge.simulator.camel;

import com.tacitknowledge.simulator.Adapter;
import com.tacitknowledge.simulator.ConversationScenario;
import com.tacitknowledge.simulator.SimulatorPojo;
import com.tacitknowledge.simulator.impl.ConversationScenarioImpl;

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
    /** inAdapter adapter which will take the information from the exchange
    and adapt it to SimulatorPojo for scenario execution. **/
    private Adapter inAdapter;
    /** outAdapter adapter which will take the information from the SimulatorPojo
    and adapt it to the desired format. **/
    private Adapter outAdapter;

    /**
     * Container of the scenarios to run
     */
    private List<ConversationScenarioImpl> scenarios;

    /**
     * Logger for the ScenarioExecutionWrapper class.
     */
    private static Logger logger
            = Logger.getLogger(ScenarioExecutionWrapper.class);

    /**
     * Constructor for the ScenarioExecutionWrapper.
     *
     * @param scenarios list of the scenarios to run.
     * @param inAdapter @see #inAdapter
     * @param outAdapter @see #outAdapter
     */
    public ScenarioExecutionWrapper(List<ConversationScenarioImpl> scenarios,
            Adapter inAdapter, Adapter outAdapter)
    {
        if (inAdapter == null)
        {
            logger.warn("Something is probably wrong: Inbound Adapter is null");
        }
        else
        {
            this.inAdapter = inAdapter;
        }
        if (outAdapter == null)
        {
            logger.warn("Something is probably wrong: Outbound Adapter is null");
        }
        else
        {
            this.outAdapter = outAdapter;
        }

        if (scenarios == null)
        {
            logger.warn("Something is probably wrong: Scenarios are null");
            this.scenarios = new ArrayList<ConversationScenarioImpl>();
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
        SimulatorPojo resultPojo = null;

        if (inAdapter != null)
        {
            resultPojo = inAdapter.adaptFrom(data);
        }
        else
        {
            logger.error("Inbound adapter is null");
        }

        //TODO add case when matching scenario is not found
        // here we are looking for first matching scenario and ignore all other scenarios
        for (ConversationScenario scenario : scenarios)
        {
            logger.debug("Evaluating scenario : " + scenario.toString());

            if (scenario.isActive() && scenario.matchesCondition(resultPojo))
            {
                logger.debug("Scenario : " + scenario
                        + " was matched, executing the transformation script.");

                resultPojo = scenario.executeTransformation(resultPojo);
                break;
            }
        }

        if (outAdapter != null)
        {
            data = outAdapter.adaptTo(resultPojo);
        }
        else
        {
            logger.error("Outbound adapter is null");
        }

        if (data != null)
        {
            exchange.getIn().setBody(data);
        }
        else
        {
            logger.error("Result of processing the incomming message "
                    + "by the ScenarioExecutionWrapper is null");
        }
    }
}
