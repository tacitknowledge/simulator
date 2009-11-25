package com.tacitknowledge.simulator.impl;

import com.tacitknowledge.simulator.ConversationScenario;
import com.tacitknowledge.simulator.SimulatorException;
import com.tacitknowledge.simulator.SimulatorPojo;
import com.tacitknowledge.simulator.scripting.PojoClassGenerator;
import com.tacitknowledge.simulator.scripting.ScriptException;
import com.tacitknowledge.simulator.scripting.ScriptExecutionService;
import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.NotFoundException;
import org.apache.log4j.Logger;

import java.util.Map;

/**
 * A wrapper for Conversation Scenarios, containing all the information needed for its execution
 *
 * @author Jorge Galindo (jgalindo@tacitknowledge.com)
 */
public class ConversationScenarioImpl implements ConversationScenario
{
    /**
     * Logger for this class.
     */
    private static Logger logger = Logger.getLogger(ConversationScenarioImpl.class);

    /**
     * Prime number to be used in the hashcode method.
     */
    private static final int HASH_CODE_PRIME = 29;

    /**
     * The scripting language used for this conversation's scenarios
     */
    private String scriptLanguage;

    /**
     * The script used to simulate a validation or process in the SUE against the entry data
     */
    private String criteriaScript;

    /**
     * The script used to transform/modify/generate the scenario output
     */
    private String transformationScript;

    /** Script Execution service which will run the actual simulation on the data received **/
    private ScriptExecutionService execServ;

    /** Generates the classes for the incoming data **/
    private PojoClassGenerator generator;

    /** Beans needed for the script executions service to run the simulation against **/
    private Map scriptExecutionBeans;

    /**
     * Whether this scenario is active or not
     */
    private boolean active;

    /**
     * Constructor for the conversation scenario class
     *
     * @param scriptLanguage
     *            the scripting language used in the simulation
     * @param criteriaScript
     *            the criteria script to match
     * @param transformationScript
     *            the transformation script for the scenario.
     */
    public ConversationScenarioImpl(String scriptLanguage, String criteriaScript,
            String transformationScript)
    {
        this.scriptLanguage = scriptLanguage;
        this.criteriaScript = criteriaScript;
        this.transformationScript = transformationScript;

        this.execServ = new ScriptExecutionService();
        this.execServ.setLanguage(scriptLanguage);

        this.generator = new PojoClassGenerator(ClassPool.getDefault());
    }

    /**
     * {@inheritDoc}
     */
    public void setActive(boolean active)
    {
        this.active = active;
    }

    /**
     * {@inheritDoc}
     */
    public boolean isActive()
    {
        return active;
    }

    /**
     * {@inheritDoc}
     *
     * @throws ScriptException
     */
    public SimulatorPojo executeTransformation(SimulatorPojo pojo) throws ScriptException
    {
        generateClasses(pojo);

       Object result =  execServ.eval(transformationScript, "Transformation Script", scriptExecutionBeans);

        //TODO Call the not yet implemented method to transform the
        //scriptExecutionBeans into SimulatorPojo
        SimulatorPojo resultPojo = transformToSimulatorPojo(result);
        return resultPojo;
    }

    private SimulatorPojo transformToSimulatorPojo(Object result) {
        return null; 
    }

    /**
     * {@inheritDoc}
     *
     * @throws ScriptException
     * @throws NotFoundException
     * @throws CannotCompileException
     */
    public boolean matchesCondition(SimulatorPojo pojo) throws ScriptException
    {
        generateClasses(pojo);

        Object result = execServ.eval(criteriaScript, "Criteria Script", scriptExecutionBeans);
        
        if (result != null && result instanceof Boolean)
        {
            return ((Boolean) result).booleanValue();
        }
        else
        {
            return false;
        }
    }

    /**
     * Determines if the current conversation scenario object is equal to the one supplied as
     * parameter.
     *
     * @param o
     *            Conversation scenario object to be compared with current one.
     * @return true if the objects are considered equal, false otherwise
     */
    public boolean equals(Object o)
    {
        if (this == o)
        {
            return true;
        }
        if (o == null || getClass() != o.getClass())
        {
            return false;
        }

        ConversationScenarioImpl that = (ConversationScenarioImpl) o;

        if (active != that.active)
        {
            return false;
        }
        if (!criteriaScript.equals(that.criteriaScript))
        {
            return false;
        }
        if (!scriptLanguage.equals(that.scriptLanguage))
        {
            return false;
        }
        if (!transformationScript.equals(that.transformationScript))
        {
            return false;
        }

        return true;
    }

    /**
     * Override for the hashcode.
     *
     * @return hashcode
     */
    public int hashCode()
    {
        int result = scriptLanguage.hashCode();
        result = HASH_CODE_PRIME * result + criteriaScript.hashCode();
        result = HASH_CODE_PRIME * result + transformationScript.hashCode();

        if (active)
        {
            result = HASH_CODE_PRIME * result + 1;
        }
        else
        {
            result = HASH_CODE_PRIME * result + 0;
        }

        return result;
    }

    /**
     * Generates the classes from the incoming data and registers them in the class pool.
     * @param pojo incoming data pojo
     * @throws ScriptException in case an exception has occured.
     */
    private void generateClasses(SimulatorPojo pojo) throws ScriptException
    {
        if (scriptExecutionBeans == null)
        {
            try
            {
                scriptExecutionBeans = generator.generateBeansMap(pojo);
            }
            catch (CannotCompileException e)
            {
                String errorMessage = "A compilation error has occured when "
                    + "generating classes for SimulatorPojo";
                logger.error(errorMessage, e);
                throw new ScriptException("error_message", e);
            }
            catch (NotFoundException e)
            {
                String errorMessage = "A class was not found in the ClassPool";
                logger.error(errorMessage, e);
                throw new ScriptException("error_message", e);
            }
            catch(SimulatorException se)
            {
                String errorMsg = "SimulatorPojo was not properly generated: " + se.getMessage();
                logger.error(errorMsg, se);
                throw new ScriptException("error_message", se);
            }
        }
    }
    
}
