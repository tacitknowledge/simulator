package com.tacitknowledge.simulator;

/**
 * This exception must be thrown if user trys to do something with scenario which doens't exist
 *
 * @author Nikita Belenkiy (nbelenkiy@tacitknowledge.com)
 */
public class ConversationNotFoundException extends Exception
{
    /**
     * Constructor for the ConversationNotFoundException class
     *
     * @param s exception message
     */
    public ConversationNotFoundException(final String s)
    {
        super(s);
    }
}
