package com.tacitknowledge.simulator.formats;

import com.tacitknowledge.simulator.Adapter;
import com.tacitknowledge.simulator.BaseConfigurable;
import com.tacitknowledge.simulator.Configurable;
import com.tacitknowledge.simulator.ConfigurableException;
import com.tacitknowledge.simulator.FormatAdapterException;
import com.tacitknowledge.simulator.SimulatorException;
import com.tacitknowledge.simulator.SimulatorPojo;
import com.tacitknowledge.simulator.configuration.ParameterDefinitionBuilder;
import com.tacitknowledge.simulator.scripting.ObjectMapperException;
import com.tacitknowledge.simulator.scripting.PojoClassGenerator;
import com.tacitknowledge.simulator.scripting.ScriptException;
import com.tacitknowledge.simulator.scripting.SimulatorPojoPopulatorImpl;
import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.NotFoundException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.camel.Exchange;

/**
 * Base class for Adapter implementations.
 *
 * @author galo
 */
public abstract class BaseAdapter extends BaseConfigurable implements Adapter<Object>
{
    /**
     * Line separator constant. Available for all adapters
     */
    protected static final String LINE_SEP = System.getProperty("line.separator");

    /**
     * Constructor
     */
    public BaseAdapter()
    {
        super();
    }

    /**
     * @inheritDoc
     * @param parameters
     */
    public BaseAdapter(Map<String, String> parameters)
    {
        super(parameters);
    }

    /**
     * This should be the second prefered constructor method from within JAVA.
     *
     * @param bound      Configurable bound
     * @param parameters Parameter values
     */
    protected BaseAdapter(int bound, Map<String, String> parameters)
    {
        super(bound, parameters);
    }

    /**
     * 
     * @param o The Camel exchange
     * @return A Map of custom-generated beans generated from the input data
     * @throws ConfigurableException
     * @throws FormatAdapterException
     */
    public Map<String, Object> generateBeans(Exchange o)
            throws ConfigurableException, FormatAdapterException
    {
        validateParameters();
        SimulatorPojo pojo = createSimulatorPojo(o);
        return generateClasses(pojo);
    }

    /**
     *
     * @param o The Camel exchange
     * @return The generated SimulatorPojo
     * @throws FormatAdapterException
     */
    protected SimulatorPojo createSimulatorPojo(Exchange o) throws FormatAdapterException
    {
        throw new UnsupportedOperationException("Override me");
    }

    /**
     *
     * @param scriptExecutionResult The object returned by the scenario excecution script
     * @param exchange The Camel exchange
     * @return A String object in the requested format representing the script result
     * @throws FormatAdapterException If any other error occurs
     */
    protected Object getString(SimulatorPojo scriptExecutionResult, Exchange exchange)
            throws FormatAdapterException
    {
        throw new UnsupportedOperationException("Override me");
    }

    private SimulatorPojo getSimulatorPojo(Object object) throws ObjectMapperException
    {
        return SimulatorPojoPopulatorImpl.getInstance().populateSimulatorPojoFromBean(object);
    }

    /**
     * @inheritDoc
     * @param scriptExecutionResult
     * @param exchange
     * @return
     * @throws ConfigurableException
     * @throws FormatAdapterException
     */
    public Object adaptTo(Object scriptExecutionResult, Exchange exchange)
            throws ConfigurableException, FormatAdapterException
    {
        validateParameters();
        
        SimulatorPojo getSimulatorPojo;
        try
        {
            getSimulatorPojo = getSimulatorPojo(scriptExecutionResult);
        }
        catch (ObjectMapperException e)
        {
            throw new FormatAdapterException("Error trying to generate temporary classes", e);
        }
        return getString(getSimulatorPojo, exchange);
    }

    /**
     * Generates the classes from the incoming data and registers them in the class pool.
     *
     * @param pojo incoming data pojo
     * @return
     * @throws com.tacitknowledge.simulator.scripting.ScriptException
     *          in case an exception has occured.
     */
    protected Map<String, Object> generateClasses(SimulatorPojo pojo) throws FormatAdapterException
    {
        Map<String, Object> scriptExecutionBeans;
        try
        {
            /**
             * Generates the classes for the incoming data *
             */
            PojoClassGenerator generator = new PojoClassGenerator(ClassPool.getDefault());

            scriptExecutionBeans = generator.generateBeansMap(pojo);
        }
        catch (CannotCompileException e)
        {
            String errorMessage = "A compilation error has occured when "
                + "generating classes for SimulatorPojo";
            throw new FormatAdapterException(errorMessage, e);
        }
        catch (NotFoundException e)
        {
            String errorMessage = "A class was not found in the ClassPool";
            throw new FormatAdapterException(errorMessage, e);
        }
        catch (SimulatorException se)
        {
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
