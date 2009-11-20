package com.tacitknowledge.simulator.impl;

import org.apache.log4j.Logger;

import com.tacitknowledge.simulator.ConversationScenario;
import com.tacitknowledge.simulator.SimulatorPojo;

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

    /**
     * Whether this scenario is active or not
     */
    private boolean active;

    /**
     * Constructor for the conversation scenario class
     * @param scriptLanguage the scripting language used in the simulation
     * @param criteriaScript the criteria script to match
     * @param transformationScript the transformation script for the scenario.
     */
    public ConversationScenarioImpl(String scriptLanguage, String criteriaScript,
            String transformationScript)
    {
        this.scriptLanguage = scriptLanguage;
        this.criteriaScript = criteriaScript;
        this.transformationScript = transformationScript;
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
     */
    public SimulatorPojo executeTransformation(SimulatorPojo pojo)
    {
        //TODO Implement this functionality.
        return null;
    }

    /**
     * {@inheritDoc}
     */
    public boolean matchesCondition(Object data)
    {
        //TODO Implement this functionality.
        return false;
    }

    /**
     * Determines if the current conversation scenario object is
     * equal to the one supplied as parameter.
     * @param o Conversation scenario object to be compared with current one.
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
}
