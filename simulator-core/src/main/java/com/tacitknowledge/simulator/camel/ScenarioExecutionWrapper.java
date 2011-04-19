package com.tacitknowledge.simulator.camel;

import com.tacitknowledge.simulator.Adapter;
import com.tacitknowledge.simulator.Conversation;
import com.tacitknowledge.simulator.Scenario;
import com.tacitknowledge.simulator.configuration.EventDispatcher;
import com.tacitknowledge.simulator.configuration.SimulatorEventType;
import org.apache.camel.Exchange;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
     * Logger for the ScenarioExecutionWrapper class.
     */
    private static Logger logger  = LoggerFactory.getLogger(ScenarioExecutionWrapper.class);
    
    /**
     * The conversation related to this execution.
     */
    private Conversation conversation;

    /**
     * Constructor for the ScenarioExecutionWrapper.
     *
     * @param conversation Conversation
     */
    public ScenarioExecutionWrapper(final Conversation conversation)
    {
        this.conversation = conversation;
    }

    /**
     * Processes the body object received from the previous step of the route. Iterates through
     * all of the provided scenarios and return the processed result of the first matched scenario.
     *
     * @param exchange body.getIn().getBody() contains data from inbound adapter
     * @return
     * @throws Exception in case of error.
     */
    public void process(final Exchange exchange) throws Exception
    {
        dispatchEvent(SimulatorEventType.NEW_MESSAGE, exchange);
        
        Adapter inAdapter = conversation.getInboundAdapter();
        Adapter outAdapter = conversation.getOutboundAdapter();
        Collection<Scenario> scenarios = conversation.getScenarios().values();
        Map<String, Object> scriptExecutionBeans = inAdapter.generateBeans(exchange);

        Object result = null;
        // here we are looking for first matching scenario and ignore all other scenarios
        for (Scenario scenario : scenarios)
        {
            synchronized (scenario)
            {
                logger.info("Evaluating scenario : {}", scenario.toString());

                boolean matchesCondition = scenario.matchesCondition(scriptExecutionBeans);
                logger.info("matches condition: {}", matchesCondition);
                
                if (matchesCondition)
                {
                    dispatchEvent(SimulatorEventType.SCENARIO_MATCHED, exchange);

                    logger.info("Executing the transformation script.");
                    result = scenario.executeTransformation(scriptExecutionBeans);
                    
                    dispatchEvent(SimulatorEventType.RESPONSE_BUILT, exchange);
                    break;
                }
            }
        }
        
        exchange.getOut().setBody(outAdapter.adaptTo(result, exchange));
    }

    private void dispatchEvent(SimulatorEventType eventType, Exchange exchange) {
        EventDispatcher.getInstance().dispatchEvent(eventType, conversation, exchange);
    }
}
