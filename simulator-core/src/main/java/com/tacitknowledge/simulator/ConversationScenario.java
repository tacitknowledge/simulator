package com.tacitknowledge.simulator;

import com.tacitknowledge.simulator.scripting.ScriptException;

/**
 * Defines the contract for the ConversationScenario.
 *
 * @author Alexandru Dereveanco (adereveanco@tacitknowledge.com)
 *
 */
public interface ConversationScenario
{
    /**
     * Updates criteria script
     *
     * @param criteriaScript new criteria script
     * @param transformationScript  new transformation script
     * @param language new language
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
     * @param pojo The scenario entry data
     * @return the transformed SimulatorPojo object
     * @throws ScriptException in case there is an exception running the script
     * @throws SimulatorException is case something else bad happens
     */
    SimulatorPojo executeTransformation(SimulatorPojo pojo) throws ScriptException, SimulatorException;

    /**
     * Veryfies if the entry data matches the criteria
     *
     * @param pojo SimulatorPojo with data to compare
     * @return True is the data matches the contained criteria, false otherwise
     * @throws ScriptException in case a script execution exception occured
     */
    boolean matchesCondition(SimulatorPojo pojo) throws ScriptException;


    /**
     *
     * @return script language id
     */
    String getScriptLanguage();

    /**
     * 
     * @return
     */
    String getCriteriaScript();

    /**
     *
     * @return
     */
    String getTransformationScript();
}
