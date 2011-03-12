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
     * @param scriptExecutionBeans The beans that need to be available to the script
     * @return True is the data matches the contained criteria, false otherwise
     * @throws ScriptException in case a script execution exception occured
     */
    boolean matchesCondition(Map<String, Object> scriptExecutionBeans) throws ScriptException;


    /**
     * @return script language id
     */
    String getScriptLanguage();

    /**
     * @return The criteria script
     */
    String getCriteriaScript();

    /**
     * @return The transformation script
     */
    String getTransformationScript();
    
    /**
     * last modified date of scenario file
     * @return long
     */
    long getLastModifiedDate();
    
    /**
     * setter for lastModifiedDate field
     * @param lastModifiedDate
     */
    void setLastModifiedDate(long lastModifiedDate);
}
