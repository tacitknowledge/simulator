package com.tacitknowledge.simulator.formats;

import com.tacitknowledge.simulator.Adapter;
import com.tacitknowledge.simulator.ConfigurableException;
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
import javax.servlet.http.HttpServletResponse;

/**
 * Implementation of the Adapter interface for the REST format
 *
 * @author Raul Huerta (rhuerta@tacitknowledge.com)
 */

public class RestAdapter extends BaseAdapter implements Adapter<Object>
{

    public static final String PARAM_EXTRACTION_PATTERN = "extractionPattern";

    private static final String PARAM_OBJECT_NAME = "objectName";

    private static final String RESPONSE = "response";

    private static final String REQUEST = "request";

    private static final String RESOURCE = "resource";
    /**
     * Adapter parameters definition.
     */
    private List<ParameterDefinitionBuilder.ParameterDefinition> parametersList =
            parameters()
                    .add(
                            name(PARAM_EXTRACTION_PATTERN).
                                    label("Pattern used to generate objects. (e.g. URL: '/system/1' with pattern:'/system/:system_id' will generate a " +
                                            "'system' object with an attribute call'system_id' equals to '1')").
                                    inOnly().required()
                    ).add(
                    name(PARAM_OBJECT_NAME).
                            label("Object Name to access attributes from the execution script.").
                            inOnly().required()
            );

    /**
     * Logger for this class.
     */
    private static Logger logger = Logger.getLogger(RestAdapter.class);

    /**
     * @inheritDoc
     */
    public RestAdapter()
    {
        super();
    }

    /**
     * @inheritDoc
     */
    public RestAdapter(int bound, Map<String, String> parameters)
    {
        super(bound, parameters);
    }

    /**
     * @throws ConfigurableException
     * @inheritDoc
     */
    @Override
    protected void validateParameters() throws ConfigurableException
    {
        if (getParamValue(PARAM_EXTRACTION_PATTERN) != null)
        {
            throw new ConfigurableException("Extraction Pattern parameter is required.");
        }
        if (getParamValue(PARAM_OBJECT_NAME) != null)
        {
            throw new ConfigurableException("Object Name parameter is required.");
        }
    }


    /**
     * @param o The Camel exchange
     * @return
     * @throws FormatAdapterException
     * @inheritDoc
     */
    @Override
    protected SimulatorPojo createSimulatorPojo(Exchange o) throws FormatAdapterException
    {

        logger.debug("Attempting to generate SimulatorPojo from REST content:\n" + o);

        HttpServletRequest request = o.getIn().getBody(HttpServletRequest.class);
        SimulatorPojo pojo = new StructuredSimulatorPojo();

        Map attributes = new HashMap<String, Object>();
//        attributes.put(REQUEST, populateRequestAttributes(request));
//        attributes.put(RESPONSE, populateResponseAttributes());

        pojo.getRoot().put(getParamValue(PARAM_OBJECT_NAME), attributes);

        logger.debug("Finished generating SimulatorPojo from REST content");
        return pojo;
    }

    /**
     * @inheritDoc
     */
    @Override
    protected Object getString(SimulatorPojo scriptExecutionResult, Exchange exchange)
            throws FormatAdapterException
    {
        Map<String, Object> pojo =
                (Map<String, Object>) scriptExecutionResult.getRoot().get(getParamValue(PARAM_OBJECT_NAME));

        StringBuffer buffer = new StringBuffer();
        if (pojo != null)
        {


        }
        HttpServletResponse response = exchange.getOut().getBody(HttpServletResponse.class);


        return response;
    }

    /**
     * Returns a List of parameters the implementing instance uses.
     * Each list element is itself a List to describe the parameter as follows:
     * <p/>
     * - 0 : Parameter name
     * - 1 : Parameter description. Useful for GUI rendition
     * - 2 : Parameter type. Useful for GUI rendition.
     * - 3 : Required or Optional parameter. Useful for GUI validation.
     * - 4 : Parameter usage. Useful for GUI rendition.
     * - 5 : Default value
     *
     * @return List of Parameters for the implementing Transport.
     * @see com.tacitknowledge.simulator.configuration.ParameterDefinitionBuilder
     * @see com.tacitknowledge.simulator.configuration.ParameterDefinitionBuilder.ParameterDefinition
     */
    @Override
    public List<List> getParametersList()
    {
        return getParametersDefinitionsAsList(parametersList);
    }
}
