package com.tacitknowledge.simulator.impl;

import com.tacitknowledge.simulator.ConversationScenario;
import com.tacitknowledge.simulator.SimulatorException;
import com.tacitknowledge.simulator.scripting.ScriptException;
import com.tacitknowledge.simulator.scripting.ScriptExecutionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
    private static Logger logger = LoggerFactory.getLogger(ConversationScenarioImpl.class);

    /**
     * Prime number to be used in the hashcode method.
     */
    private static final int HASH_CODE_PRIME = 29;

    /**
     * Scenario Id
     */
    private int scenarioId;
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

    /**
     * Script Execution service which will run the actual simulation on the data received *
     */
    private ScriptExecutionService execServ;

    /**
     * Whether this scenario is active or not
     */
    private boolean active = true;

    /**
     * Constructor for the conversation scenario class
     *
     * @param scenarioId           Scenario ID
     * @param scriptLanguage       the scripting language used in the simulation
     * @param criteriaScript       the criteria script to match
     * @param transformationScript the transformation script for the scenario.
     */
    public ConversationScenarioImpl(final int scenarioId, final String scriptLanguage,
                                    final String criteriaScript, final String transformationScript)
    {
        this.scenarioId = scenarioId;
        this.scriptLanguage = scriptLanguage;
        this.criteriaScript = criteriaScript;
        this.transformationScript = transformationScript;

        this.execServ = new ScriptExecutionService();
        this.execServ.setLanguage(scriptLanguage);

    }

    /**
     * {@inheritDoc}
     */
    public int getScenarioId()
    {
        return scenarioId;
    }

    /**
     * {@inheritDoc}
     */
    public synchronized void setScripts(final String criteriaScriptValue,
                                        final String transformationScriptValue,
                                        final String language)
    {
        this.criteriaScript = criteriaScriptValue;
        this.transformationScript = transformationScriptValue;
        this.scriptLanguage = language;
    }

    /**
     * {@inheritDoc}
     */
    public void setActive(final boolean active)
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
     * @param scriptExecutionBeans
     * @throws ScriptException
     */
    public Object executeTransformation(final Map<String, Object> scriptExecutionBeans) throws
            ScriptException, SimulatorException
    {
        return execServ.eval(transformationScript, "Transformation Script", scriptExecutionBeans);
    }


    /**
     * {@inheritDoc}
     *
     * @param scriptExecutionBeans
     * @throws ScriptException
     */
    public boolean matchesCondition(final Map<String, Object> scriptExecutionBeans) throws
            ScriptException
    {

        Object result = execServ.eval(criteriaScript, "Criteria Script", scriptExecutionBeans);

        if (result != null && result instanceof Boolean)
        {
            return (Boolean) result;
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
     * @param o Conversation scenario object to be compared with current one.
     * @return true if the objects are considered equal, false otherwise
     */
    public boolean equals(final Object o)
    {
        boolean result = true;
        if (this == o)
        {
            result = true;
        }
        if (o == null || getClass() != o.getClass())
        {
            result = false;
        }

        ConversationScenarioImpl that = (ConversationScenarioImpl) o;

        if (active != that.active)
        {
            result = false;
        }
        if (!criteriaScript.equals(that.criteriaScript))
        {
            result = false;
        }
        if (!scriptLanguage.equals(that.scriptLanguage))
        {
            result = false;
        }
        if (!transformationScript.equals(that.transformationScript))
        {
            result = false;
        }

        return result;
    }

    /**
     * {@inheritDoc}
     */
    public String getScriptLanguage()
    {
        return scriptLanguage;
    }

    /**
     * {@inheritDoc}
     */
    public String getCriteriaScript()
    {
        return criteriaScript;
    }

    /**
     * {@inheritDoc}
     */
    public String getTransformationScript()
    {
        return transformationScript;
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

    @Override
    public String toString()
    {
        return "ConversationScenarioImpl{"
                + "scenarioId=" + scenarioId
                + ", scriptLanguage='" + scriptLanguage + '\''
                + ", criteriaScript='" + criteriaScript + '\''
                + ", transformationScript='" + transformationScript + '\''
                + ", execServ=" + execServ
                + ", active=" + active
                + '}';
    }
}
