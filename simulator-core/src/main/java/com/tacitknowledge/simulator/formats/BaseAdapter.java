package com.tacitknowledge.simulator.formats;

import com.tacitknowledge.simulator.Adapter;
import com.tacitknowledge.simulator.FormatAdapterException;
import com.tacitknowledge.simulator.SimulatorException;
import com.tacitknowledge.simulator.SimulatorPojo;
import com.tacitknowledge.simulator.scripting.ObjectMapperException;
import com.tacitknowledge.simulator.scripting.PojoClassGenerator;
import com.tacitknowledge.simulator.scripting.ScriptException;
import com.tacitknowledge.simulator.scripting.SimulatorPojoPopulatorImpl;
import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.NotFoundException;

import java.util.HashMap;
import java.util.Map;

/**
 * Base class for Adapter implementations.
 *
 * @author galo
 */
public abstract class BaseAdapter implements Adapter<Object>
{
    /**
     * Line separator constant. Available for all adapters
     */
    protected static final String LINE_SEP = System.getProperty("line.separator");

    /**
     * The Adapter parameters. Each Adapter implementation should define its corresponding
     * parameters.
     */
    private Map<String, String> parameters = new HashMap<String, String>();

    /**
     * Constructor
     */
    public BaseAdapter()
    {
    }

    /**
     * Constructor
     *
     * @param parameters @see #parameters
     */
    public BaseAdapter(Map<String, String> parameters)
    {
        this.parameters = parameters;
    }


    /**
     * @inheritDoc
     * @param parameters The Adapter parameters Map
     */
    public void setParameters(Map<String, String> parameters)
    {
        this.parameters = parameters;
    }

    /**
     * Sets and/or overrides instance variables from provided parameters and validates that
     * the required parameters are present.
     * @throws FormatAdapterException If any required parameter is missing or incorrect
     */
    abstract void validateParameters() throws FormatAdapterException;

    public Map<String, Object> generateBeans(String o) throws FormatAdapterException
    {
        validateParameters();
        SimulatorPojo pojo = createSimulatorPojo(o);
        return generateClasses(pojo);
    }

    protected SimulatorPojo createSimulatorPojo(String o) throws FormatAdapterException
    {
        throw new UnsupportedOperationException("override me");
    }


    protected String getString(SimulatorPojo scriptExecutionResult) throws FormatAdapterException
    {
        throw new UnsupportedOperationException("override me");
    }
    
    /**
     * @param name The parameter name. Parameter names should be defined by each implementation.
     * @return The parameter value or null if not defined.
     */
    protected String getParamValue(String name)
    {
        return parameters.get(name);
    }

    private SimulatorPojo getSimulatorPojo(Object object) throws ObjectMapperException
    {
        return SimulatorPojoPopulatorImpl.getInstance().populateSimulatorPojoFromBean(object);
    }


    public String adaptTo(Object scriptExecutionResult) throws FormatAdapterException
    {
        validateParameters();
        SimulatorPojo getSimulatorPojo;
        try
        {
            getSimulatorPojo = getSimulatorPojo(scriptExecutionResult);
        }
        catch (ObjectMapperException e)
        {
            throw new FormatAdapterException("Error trying to generate temporary classes",e);
        }
        return getString(getSimulatorPojo);
    }

    /**
     * Generates the classes from the incoming data and registers them in the class pool.
     *
     * @param pojo incoming data pojo
     * @throws com.tacitknowledge.simulator.scripting.ScriptException
     *          in case an exception has occured.
     * @return
     */
    protected Map<String, Object> generateClasses(SimulatorPojo pojo) throws FormatAdapterException
    {
        Map<String, Object> scriptExecutionBeans;
        try {
            /**
             * Generates the classes for the incoming data *
             */
            PojoClassGenerator generator = new PojoClassGenerator(ClassPool.getDefault());

            scriptExecutionBeans = generator.generateBeansMap(pojo);
        }
        catch (CannotCompileException e) {
            String errorMessage = "A compilation error has occured when "
                    + "generating classes for SimulatorPojo";
            throw new FormatAdapterException(errorMessage, e);
        }
        catch (NotFoundException e) {
            String errorMessage = "A class was not found in the ClassPool";
            throw new FormatAdapterException(errorMessage, e);
        }
        catch (SimulatorException se) {
            String errorMsg = "SimulatorPojo was not properly generated: " + se.getMessage();
            throw new FormatAdapterException(errorMsg, se);
        }
        catch (ScriptException e)
        {
            throw new FormatAdapterException("", e);
        }
        return scriptExecutionBeans;
    }
}
