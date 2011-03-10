package com.tacitknowledge.simulator.scripting;

/**
 * Exception thrown by <code>ScriptRunner</code> upon a script execution error.
 *
 * @author Alexandru Dereveanco (adereveanco@tacitknowledge.com)
 */
@SuppressWarnings("serial")
public class ScriptException extends Exception
{
    /**
     * Creates a new ScriptException.
     *
     * @param message a brief message about the error
     * @param cause   the root cause of the error
     */
    public ScriptException(final String message, final Throwable cause)
    {
        super(message, cause);
    }
}
