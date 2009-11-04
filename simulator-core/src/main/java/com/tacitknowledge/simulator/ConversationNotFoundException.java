package com.tacitknowledge.simulator;

/**
 * This exception must be thrown if user trys to do something with scenario which doens't exist
 *
 * @author nikitabelenkiy
 */
public class ConversationNotFoundException extends Exception
{
    public ConversationNotFoundException(String s)
    {
        super(s);
    }
}
