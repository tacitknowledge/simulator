package com.tacitknowledge.simulator.formats;

import com.tacitknowledge.simulator.Adapter;
import com.tacitknowledge.simulator.FormatAdapterException;
import com.tacitknowledge.simulator.SimulatorPojo;
import com.tacitknowledge.simulator.StructuredSimulatorPojo;
import com.tacitknowledge.simulator.configuration.ParameterDefinitionBuilder;
import static com.tacitknowledge.simulator.configuration.ParameterDefinitionBuilder.name;
import static com.tacitknowledge.simulator.configuration.ParametersListBuilder.parameters;

import java.util.*;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;
import org.apache.camel.Exchange;

import javax.servlet.http.HttpServletRequest;

/**
 * Implementation of the Adapter interface for the REST format
 *
 * @author Raul Huerta (rhuerta@tacitknowledge.com)
 */

public class RestAdapter extends BaseAdapter implements Adapter<Object> {

    public static final String PARAM_PARAMETERS = "parameters";

    /**
     * Adapter parameters definition.
     */
    private List<ParameterDefinitionBuilder.ParameterDefinition> parametersList =
        parameters();

    /**
     * Logger for this class.
     */
    private static Logger logger = Logger.getLogger(RestAdapter.class);

    /**
     * @inheritDoc
     */
    public RestAdapter() {
        super();
    }

    /**
     * @inheritDoc
     */
    public RestAdapter(Map<String, String> parameters) {
        super(parameters);
    }

    /**
     * Validate that all parameters required exist
     * @throws FormatAdapterException
     */
    public void validateParameters() throws FormatAdapterException {
    }

    /**
     * Get all parameters for this adapter
     * @return List of Lists
     */
    public List<List> getParametersList() {
        return getParametersDefinitionsAsList(parametersList);
    }


    @Override
     protected SimulatorPojo createSimulatorPojo(Exchange o) throws FormatAdapterException {

        logger.debug("Attempting to generate SimulatorPojo from REST content:\n" + o);

        HttpServletRequest request = o.getIn().getBody(HttpServletRequest.class);
        SimulatorPojo pojo = new StructuredSimulatorPojo();

        //TODO in progress

        logger.debug("Finished generating SimulatorPojo from REST content");
        return pojo;
    }

    /**
     * @inheritDoc
     */
    @Override
    protected String getString(SimulatorPojo scriptExecutionResult) throws FormatAdapterException {
        Map<String, Object> pojo  = (Map<String, Object>) scriptExecutionResult.getRoot().get("root");
        StringBuffer buffer = new StringBuffer();

        //TODO in progress

        String result = buffer.toString();
        return result.substring(result.length() - 1).equals("&") ? result.substring(0, buffer.length() - 1) : result;
    }
}
