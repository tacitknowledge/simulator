package com.tacitknowledge.simulator;

/**
 * A wrapper for Conversation Scenarios, containing all the information needed for its execution
 *
 * @author Jorge Galindo (jgalindo@tacitknowledge.com)
 */
public class ConversationScenario
{
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
     * @param scriptLanguage
     * @param criteriaScript
     * @param transformationScript
     */
    public ConversationScenario(String scriptLanguage, String criteriaScript, String transformationScript)
    {
        this.scriptLanguage = scriptLanguage;
        this.criteriaScript = criteriaScript;
        this.transformationScript = transformationScript;
    }

    /**
     * Sets the active flag
     *
     * @param active
     */
    public void setActive(boolean active)
    {
        this.active = active;
    }

    /**
     * Returns if this scenarios is active or not
     *
     * @return
     */
    public boolean isActive()
    {
        return active;
    }

    /**
     * Starts this scenario execution with the provided entry data
     *
     * @param pojo The scenario entry data
     * @return
     */
    public Object run(SimulatorPojo pojo)
    {
        return null;
    }


    /**
     * Veryfies if the entry data matches the criteria
     *
     * @param data The data to compare
     * @return True is the data matches the contained criteria, false otherwise
     */
    public boolean matchesCondition(Object data)
    {
        return false;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ConversationScenario that = (ConversationScenario) o;

        if (active != that.active) return false;
        if (!criteriaScript.equals(that.criteriaScript)) return false;
        if (!scriptLanguage.equals(that.scriptLanguage)) return false;
        if (!transformationScript.equals(that.transformationScript)) return false;

        return true;
    }

    @Override
    public int hashCode()
    {
        int result = scriptLanguage.hashCode();
        result = 31 * result + criteriaScript.hashCode();
        result = 31 * result + transformationScript.hashCode();
        result = 31 * result + (active ? 1 : 0);
        return result;
    }
}
