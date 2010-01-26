package com.tacitknowledge.simulator;

import com.tacitknowledge.simulator.scripting.ScriptException;

import java.util.Map;

/**
 * Defines the contract for the ConversationScenario.
 *
 * @author Alexandru Dereveanco (adereveanco@tacitknowledge.com)
 */
public interface ConversationScenario
{
    /**
     * Updates criteria script
     *
     * @param criteriaScript       new criteria script
     * @param transformationScript new transformation script
     * @param language             new language
     */
    void setScripts(String criteriaScript, String transformationScript, String language);

    /**
     * Sets the active flag
     *
     * @param active @see #active
     */
    void setActive(boolean active);

    /**
     * Returns if this scenarios is active or not
     *
     * @return @see #active
     */
    boolean isActive();

    /**
     * Starts this scenario execution with the provided entry data
     *
     * @param scriptExecutionBeans - Script execution parameters
     * @return the transformed SimulatorPojo object
     * @throws ScriptException    in case there is an exception running the script
     * @throws SimulatorException is case something else bad happens
     */
    Object executeTransformation(Map<String, Object> scriptExecutionBeans) throws ScriptException,
            SimulatorException;

    /**
     * Veryfies if the entry data matches the criteria
     *
     * @param scriptExecutionBeans
     * @return True is the data matches the contained criteria, false otherwise
     * @throws ScriptException in case a script execution exception occured
     */
    boolean matchesCondition(Map<String, Object> scriptExecutionBeans) throws ScriptException;


    /**
     * @return script language id
     */
    String getScriptLanguage();

    /**
     * @return
     */
    String getCriteriaScript();

    /**
     * @return
     */
    String getTransformationScript();

    /**
     * unique scenario id in the system.
     *
     * @return
     */
    int getScenarioId();
}
