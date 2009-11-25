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
}
