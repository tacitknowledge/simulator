package com.tacitknowledge.simulator.scripting;

import org.apache.bsf.BSFException;
import org.apache.bsf.BSFManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.Map.Entry;

/**
 * Service that executes a supplied script. The service supports any BSF-compatible scripting
 * language.
 *
 * @author Alexandru Dereveanco (adereveanco@tacitknowledge.com)
 */
public class ScriptExecutionService
{

    /**
     * Logger for this class.
     */
    private static Logger logger = LoggerFactory.getLogger(ScriptExecutionService.class);


   /**
     * The name of the scripting language to use
     */
    private String language = null;

    /**
     * Executes the specified script.
     *
     * @param script     the script text to execute
     * @param scriptName the name of the script input
     * @param globals    a map of objects to expose as global variables in the script
     * @throws ScriptException if an unexpected error occurs
     */
    public void exec(final String script, final String scriptName,
                        final Map globals) throws ScriptException
    {
        try
        {
            initBSFManager(globals).exec(language, scriptName, 0, 0, script);
        }
        catch (BSFException e)
        {
            throw new ScriptException("Error executing script: ", e);
        }
    }

    /**
     * Evaluates the specified script.
     *
     * @param script     the script text to execute
     * @param scriptName the name of the script input
     * @param globals    a map of objects to expose as global variables in the script
     * @return the result of the script evaluation
     * @throws ScriptException if an unexpected error occurs
     */
    public Object eval(final String script, final String scriptName, final Map globals) throws ScriptException
    {
        try
        {
            return initBSFManager(globals).eval(language, scriptName, 0, 0, script);
        }
        catch (BSFException e)
        {
            throw new ScriptException("Error executing script: ", e);
        }
    }

    /**
     * Called immediately prior to user scripts being executed. This method can be overridden by
     * subclasses that need to customize the runtime environment (for example, running a script to
     * define methods and command that should be made available to the user script). This
     * implementation of this method in <code>ScriptExecutionService</code> does nothing.
     *
     * @param manager the newly created BSFManager
     */
    protected void configureBSFManager(final BSFManager manager)
    {
        // Nothing to do in the base class
    }

    /**
     * Returns a new, fully initialized <code>BSFManager</code> instance.
     *
     * @param globals a map of objects to expose as global variables in the script
     * @return a new, fully initialized <code>BSFManager</code> instance
     * @throws ScriptException if an unexpected error occurs
     */
    private BSFManager initBSFManager(final Map globals) throws ScriptException
    {
        BSFManager manager = new BSFManager();
        bindObjectToEngineRuntime(manager, globals);

        // Allow subclasses to customize the manager
        configureBSFManager(manager);

        return manager;
    }

    /**
     * Binds each object in the given map to the script engine's runtime state.
     *
     * @param manager the BSFManager instance
     * @param beans   the map of bean names-to-beans
     * @throws ScriptException if there was a problem adding a bean to the interpreter
     */
    private void bindObjectToEngineRuntime(
        final BSFManager manager,
        final Map<String, Object> beans)
        throws ScriptException
    {
        if (beans == null)
        {
            return;
        }

        for (Entry<String, Object> entry : beans.entrySet())
        {
            String varName = entry.getKey();
            Object varValue = entry.getValue();

            if (varValue != null)
            {
                logger.debug("Registering bean '{}' [{}]", varName, varValue.getClass().getName());
                try
                {
                    manager.declareBean(varName, varValue, varValue.getClass());
                }
                catch (BSFException e)
                {
                    throw new ScriptException("Error binding variable '" + varName + "': ", e);
                }
            }
        }
    }

    /**
     * @return Returns the language.
     */
    public String getLanguage()
    {
        return language;
    }

    /**
     * @param language The language to set.
     */
    public void setLanguage(final String language)
    {
        this.language = language;
    }


    /**
     *
     * @return An Array with the available scripting languages
     */
    public static String[][] getAvailableLanguages()
    {
        //TODO automate this
        String[][] languages = {
            {"javascript", "Java Script"},
            {"ruby", "JRuby"}
        };
        return languages;
    }
}
