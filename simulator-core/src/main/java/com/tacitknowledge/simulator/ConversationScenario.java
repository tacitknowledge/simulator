package com.tacitknowledge.simulator;

/**
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
     * @param pojo The scenario entry data
     * @return
     */
    public Object run(SimulatorPojo pojo)
    {
        return null;
    }
}
