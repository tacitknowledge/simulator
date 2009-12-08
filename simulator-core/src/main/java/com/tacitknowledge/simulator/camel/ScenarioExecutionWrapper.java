package com.tacitknowledge.simulator.camel;

import com.tacitknowledge.simulator.Adapter;
import com.tacitknowledge.simulator.ConversationScenario;
import com.tacitknowledge.simulator.SimulatorException;
import com.tacitknowledge.simulator.SimulatorPojo;
import com.tacitknowledge.simulator.scripting.PojoClassGenerator;
import com.tacitknowledge.simulator.scripting.ScriptException;
import com.tacitknowledge.simulator.scripting.SimulatorPojoPopulatorImpl;
import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.NotFoundException;
import org.apache.log4j.Logger;

import java.util.ArrayList;
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
            this.scenarios = new ArrayList<ConversationScenario>();
        }
        else
        {
            this.scenarios = scenarios;
        }
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
        SimulatorPojo data = null;

        if (inAdapter != null)
        {
            data = inAdapter.adaptFrom(body);
        }
        else
        {
            logger.error("Inbound adapter is null");
        }

        Object result = null;

        /**
         * Beans needed for the script executions service to run the simulation against *
         */
         Map<String, Object> scriptExecutionBeans=generateClasses(data);

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

        String stringResult = null;
        if (outAdapter != null) {
            if (result != null) {
                //is result is a string means that it's ready  to be sent as response.
                if (result instanceof String) {
                    return (String) result;
                }
                SimulatorPojo pojo = SimulatorPojoPopulatorImpl.getInstance().populateSimulatorPojoFromBean(result);
                stringResult = outAdapter.adaptTo(pojo);
            }
        } else {
            logger.error("Outbound adapter is null");

        }
        return stringResult;
    }

    /**
     * Generates the classes from the incoming data and registers them in the class pool.
     *
     * @param pojo incoming data pojo
     * @throws com.tacitknowledge.simulator.scripting.ScriptException
     *          in case an exception has occured.
     */
    private Map<String, Object> generateClasses(SimulatorPojo pojo) throws ScriptException {
        Map<String, Object> scriptExecutionBeans = null;
        try {
            /**
             * Generates the classes for the incoming data *
             */
            PojoClassGenerator generator = new PojoClassGenerator(ClassPool.getDefault());

            scriptExecutionBeans = generator.generateBeansMap(pojo);
        }
        catch (CannotCompileException e) {
            String errorMessage = "A compilation error has occured when "
                    + "generating classes for SimulatorPojo";
            logger.error(errorMessage, e);
            throw new ScriptException("error_message", e);
        }
        catch (NotFoundException e) {
            String errorMessage = "A class was not found in the ClassPool";
            logger.error(errorMessage, e);
            throw new ScriptException("error_message", e);
        }
        catch (SimulatorException se) {
            String errorMsg = "SimulatorPojo was not properly generated: " + se.getMessage();
            logger.error(errorMsg, se);
            throw new ScriptException("error_message", se);
        }
        return scriptExecutionBeans;
    }
}
