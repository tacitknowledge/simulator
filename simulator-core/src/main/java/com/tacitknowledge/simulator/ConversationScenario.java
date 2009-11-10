package com.tacitknowledge.simulator;

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
     * @return the transformed data object
     */
    Object executeTransformation(SimulatorPojo pojo);

    /**
     * Veryfies if the entry data matches the criteria
     *
     * @param data The data to compare
     * @return True is the data matches the contained criteria, false otherwise
     */
    boolean matchesCondition(Object data);

}
