package com.tacitknowledge.simulator.camel;

import com.tacitknowledge.simulator.Adapter;
import com.tacitknowledge.simulator.ConversationScenario;
import org.apache.log4j.Logger;

import java.util.Collection;
import java.util.Map;

/**
 * Wrapper for the scenario execution.
 *
 * @author Nikita Belenkiy (nbelenkiy@tacitknowledge.com)
 * @author Alexandru Dereveanco (adereveanco@tacitknowledge.com)
 */
public class ScenarioExecutionWrapper
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
    private Collection<ConversationScenario> scenarios;

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
    public ScenarioExecutionWrapper(Collection<ConversationScenario> scenarios,
                                    Adapter inAdapter, Adapter outAdapter)
    {
        if (inAdapter == null || outAdapter == null || scenarios == null)
        {
            logger.warn("Something is probably wrong: One of the adapters or scenarios list is null");
        }
        this.inAdapter = inAdapter;
        this.outAdapter = outAdapter;
        this.scenarios = scenarios;
    }

    /**
     * Processes the body object received from the previous step of the route. Iterates through
     * all of the provided scenarios and return the processed result of the first matched scenario.
     *
     * @param body body.getIn().getBody() contains data from inbound adapter
     * @throws Exception in case of error.
     * @return
     */
    public String process(String body) throws Exception
    {

        /**
         * Beans needed for the script executions service to run the simulation against *
         */
        Map<String, Object> scriptExecutionBeans = inAdapter.generateBeans(body);

        Object result=null;
        //TODO add case when matching scenario is not found
        // here we are looking for first matching scenario and ignore all other scenarios
        for (ConversationScenario scenario : scenarios)
        {
            synchronized (scenario) {
                logger.debug("Evaluating scenario : " + scenario.toString());
                if (scenario.isActive() && scenario.matchesCondition(scriptExecutionBeans)) {
                    logger.debug("Scenario : " + scenario
                            + " was matched, executing the transformation script.");
                    result = scenario.executeTransformation(scriptExecutionBeans);
                    break;
                }
            }
        }

        return outAdapter.adaptTo(result);
    }


}
