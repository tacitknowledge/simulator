package com.tacitknowledge.simulator.formats;

import com.tacitknowledge.simulator.*;
import com.tacitknowledge.simulator.scripting.ObjectMapperException;
import com.tacitknowledge.simulator.scripting.PojoClassGenerator;
import com.tacitknowledge.simulator.scripting.ScriptException;
import com.tacitknowledge.simulator.scripting.SimulatorPojoPopulatorImpl;
import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.NotFoundException;
import org.apache.camel.Exchange;

import java.util.Map;

/**
 * Base class for Adapter implementations.
 *
 * @author galo
 */
public abstract class BaseAdapter implements Adapter
{
    /**
     * Line separator constant. Available for all adapters
     */
    protected static final String LINE_SEP = System.getProperty("line.separator");
    protected Configurable configuration;

    /**
     * Constructor
     */
    public BaseAdapter()
    {
        this(new BaseConfigurable());
    }

    /**
     * @param configurable - parameters
     * @inheritDoc
     */
    public BaseAdapter(Configurable configurable)
    {
        this.configuration = configurable;
    }

//    /**
//     * This should be the second prefered constructor method from within JAVA.
//     *
//     * @param bound      Configurable bound
//     * @param parameters Parameter values
//     */
//    protected BaseAdapter(final int bound, final Map<String, String> parameters)
//    {
//        super(bound, parameters);
//    }

    /**
     * @param exchange The Camel exchange
     * @return A Map of custom-generated beans generated from the input data
     * @throws ConfigurableException  If any required parameter is missing.
     * @throws FormatAdapterException If any other error occurs.
     */
    public Map<String, Object> generateBeans(final Exchange exchange)
        throws ConfigurableException, FormatAdapterException
    {
        //todo - mws - figure out where to handle validations
//        configuration.validateParameters();

        SimulatorPojo pojo = createSimulatorPojo(exchange);
        return generateClasses(pojo);
    }

    /**
     * @param o The Camel exchange
     * @return The generated SimulatorPojo
     * @throws FormatAdapterException If any error occurs
     */
    protected abstract SimulatorPojo createSimulatorPojo(Exchange o) throws FormatAdapterException;

    /**
     * @param scriptExecutionResult The object returned by the scenario excecution script
     * @param exchange              The Camel exchange
     * @return A String object in the requested format representing the script result
     * @throws FormatAdapterException If any other error occurs
     */
    protected abstract String getString(SimulatorPojo scriptExecutionResult, Exchange exchange)
        throws FormatAdapterException;

    /**
     *
     * @param object The object to be used to populate a SimulatorPojo
     * @return SimulatorPojo containing the sctructured representation of the incoming Object
     * @throws ObjectMapperException If an error mapping the Object into the SimulatorPojo occurs
     */
    protected SimulatorPojo getSimulatorPojo(final Object object) throws ObjectMapperException
    {
        return SimulatorPojoPopulatorImpl.getInstance().populateSimulatorPojoFromBean(object);
    }

    /**
     * @param scriptExecutionResult The object result from the execution script
     * @param exchange The Camel Exchange
     * @return The object data in the desired format
     * @throws ConfigurableException  If any required parameter is missing.
     * @throws FormatAdapterException If any other error occurs.
     * @inheritDoc
     */
    public Object adaptTo(final Object scriptExecutionResult, final Exchange exchange)
        throws ConfigurableException, FormatAdapterException
    {
        //todo - mws - figure out where to handle validations
//        configuration.validateParameters();

        SimulatorPojo getSimulatorPojo;
        try
        {

            //currently returns "nativeobject" as root.  needs "payload" as root
            getSimulatorPojo = getSimulatorPojo(scriptExecutionResult);

        }
        catch (ObjectMapperException e)
        {
            throw new FormatAdapterException("Error trying to generate temporary classes", e);
        }
        final String result = getString(getSimulatorPojo, exchange);
        return result;
    }

    /**
     * Generates the classes from the incoming data and registers them in the class pool.
     *
     * @param pojo incoming data pojo
     * @return Map containing the generated classes
     * @throws FormatAdapterException If any error occurs
     */
    protected Map<String, Object> generateClasses(final SimulatorPojo pojo)
        throws FormatAdapterException
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
