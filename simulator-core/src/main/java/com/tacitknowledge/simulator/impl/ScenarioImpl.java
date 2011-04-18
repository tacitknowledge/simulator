package com.tacitknowledge.simulator.impl;

import java.util.Map;

import com.tacitknowledge.simulator.Scenario;
import com.tacitknowledge.simulator.SimulatorException;
import com.tacitknowledge.simulator.scripting.ScriptException;
import com.tacitknowledge.simulator.scripting.ScriptExecutionService;
import com.tacitknowledge.simulator.utils.ConversationUtil;

/**
 * A wrapper for Conversation Scenarios, containing all the information needed for its execution
 *
 * @author Jorge Galindo (jgalindo@tacitknowledge.com)
 */
public class ScenarioImpl implements Scenario
{
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

    /**
     * Script Execution service which will run the actual simulation on the data received *
     */
    private ScriptExecutionService execServ;

    /**
     * last modified date of this scenario file
     */
    private long lastModifiedDate;

    /**
     * scenario configuration file path
     */
    private String configFilePath;

    /**
     * Constructor for the conversation scenario class
     *
     * @param scriptLanguage       the scripting language used in the simulation
     * @param criteriaScript       the criteria script to match
     * @param transformationScript the transformation script for the scenario.
     */
    public ScenarioImpl(final String configFilePath, final String scriptLanguage,
            final String criteriaScript, final String transformationScript)
    {
        this.scriptLanguage = scriptLanguage;
        this.criteriaScript = criteriaScript;
        this.transformationScript = transformationScript;

        this.execServ = new ScriptExecutionService(scriptLanguage);

        this.configFilePath = configFilePath;
        this.lastModifiedDate = ConversationUtil.getFileModifiedDate(configFilePath);
    }

    /**
     * {@inheritDoc}
     */
    public long getLastModifiedDate()
    {
        return this.lastModifiedDate;
    }

    /**
     * {@inheritDoc}
     */
    public String getConfigurationFilePath()
    {
        return this.configFilePath;
    }

    /**
     * {@inheritDoc}
     *
     * @param scriptExecutionBeans
     * @throws ScriptException
     */
    public Object executeTransformation(final Map<String, Object> scriptExecutionBeans)
            throws ScriptException, SimulatorException
    {
        return execServ.eval(transformationScript, "Transformation Script", scriptExecutionBeans);
    }

    /**
     * {@inheritDoc}
     *
     * @param scriptExecutionBeans
     * @throws ScriptException
     */
    public boolean matchesCondition(final Map<String, Object> scriptExecutionBeans)
            throws ScriptException
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
        else if (o == null || getClass() != o.getClass())
        {
            result = false;
        }

        ScenarioImpl that = (ScenarioImpl) o;

        if (!criteriaScript.equals(that.criteriaScript)
                || !scriptLanguage.equals(that.scriptLanguage)
                || !transformationScript.equals(that.transformationScript))
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
        return result;
    }

    @Override
    public String toString()
    {
        return "ConversationScenarioImpl{" + ", scriptLanguage='" + scriptLanguage + '\''
                + ", criteriaScript='" + criteriaScript + '\'' + ", transformationScript='"
                + transformationScript + '\'' + ", execServ=" + execServ + "}";
    }
}
