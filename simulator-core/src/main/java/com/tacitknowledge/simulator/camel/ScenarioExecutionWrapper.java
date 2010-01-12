package com.tacitknowledge.simulator.camel;

import com.tacitknowledge.simulator.Adapter;
import com.tacitknowledge.simulator.ConversationScenario;
import com.tacitknowledge.simulator.Conversation;
import com.tacitknowledge.simulator.configuration.EventDispatcher;
import com.tacitknowledge.simulator.configuration.SimulatorEventType;
import org.apache.log4j.Logger;
import org.apache.camel.Exchange;

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
    /**
     * inAdapter adapter which will take the information from the exchange
     * and adapt it to SimulatorPojo for scenario execution. *
     */
    private Adapter inAdapter;
    /**
     * outAdapter adapter which will take the information from the SimulatorPojo
     * and adapt it to the desired format. *
     */
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
     * The conversation related to this execution.
     */
    private Conversation conversation;

    /**
     * Constructor for the ScenarioExecutionWrapper.
     *
     * @param conv
     */
    public ScenarioExecutionWrapper(Conversation conv)
    {
        this.conversation = conv;

        this.inAdapter = conv.getInboundAdapter();
        this.outAdapter = conv.getOutboundAdapter();
        this.scenarios = conv.getScenarios();;

        if (this.inAdapter == null || this.outAdapter == null || this.scenarios == null)
        {
            logger.warn("Something is probably wrong: One of the adapters or scenarios list is null");
        }
    }

    /**
     * Processes the body object received from the previous step of the route. Iterates through
     * all of the provided scenarios and return the processed result of the first matched scenario.
     *
     * @param body body.getIn().getBody() contains data from inbound adapter
     * @return
     * @throws Exception in case of error.
     */
    public String process(Exchange body) throws Exception
    {
        /**
         * Beans needed for the script executions service to run the simulation against *
         */
        Map<String, Object> scriptExecutionBeans = inAdapter.generateBeans(body);

        Object result = null;
        //TODO add case when matching scenario is not found
        // here we are looking for first matching scenario and ignore all other scenarios

        String messageBody = body.getIn().getBody(String.class);
        for (ConversationScenario scenario : scenarios)
        {
            synchronized (scenario)
            {
                logger.info("Evaluating scenario : " + scenario.toString());
                boolean active = scenario.isActive();
                boolean matchesCondition = scenario.matchesCondition(scriptExecutionBeans);
                logger.info("active: " + active + " matches condition: " + matchesCondition);
                if (active && matchesCondition)
                {
                    EventDispatcher.getInstance().dispatchEvent(SimulatorEventType.SCENARIO_MATCHED, this.conversation, messageBody);

                    logger.info("Executing the transformation script.");
                    result = scenario.executeTransformation(scriptExecutionBeans);

                    EventDispatcher.getInstance().dispatchEvent(SimulatorEventType.RESPONSE_BUILT, this.conversation, messageBody);
                    break;
                }
            }
        }        
        return outAdapter.adaptTo(result);
    }


}
