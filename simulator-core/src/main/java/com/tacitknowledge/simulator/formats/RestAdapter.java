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

    private static final String PARAM_RESOURCE = "resource";

    private static final String PARAM_HTTP_METHOD = "httpMethod";

    private static final String PARAM_ACTION = "action";

    private static final String PARAM_REQUEST_URI = "requestURI";
    /**
     * Adapter parameters definition.
     */
    private List<ParameterDefinitionBuilder.ParameterDefinition> parametersList =
        parameters().
            add(
                name(PARAM_RESOURCE).
                    label("Resource requested (e.g. employee(s), order(s), etc.)").
                    required()
            );

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
        if (getParamValue(PARAM_RESOURCE) == null) {
            throw new FormatAdapterException("Resource Requested parameter is required.");
        }
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

        //set the resource name
        pojo.getRoot().put(PARAM_RESOURCE, getParamValue(PARAM_RESOURCE));

        //set http method
        pojo.getRoot().put(PARAM_HTTP_METHOD, request.getMethod());

        //set request uri
        //set http method
        pojo.getRoot().put(PARAM_REQUEST_URI, request.getRequestURI());

        //set action. Action is indicated by the second resource requested (if any).
        //e.g. /systems/create/1 where 'create' is the action
        String requestURI = request.getRequestURI();
        String[] resources = requestURI.split("/");
        if(resources.length > 2) {
            pojo.getRoot().put(PARAM_ACTION, resources[1]);
        }

        //set request params
        Map parameterMap = request.getParameterMap();
        if(parameterMap != null && parameterMap.entrySet().size() > 0) {
            Iterator i = parameterMap.entrySet().iterator();
            while(i.hasNext()) {
                Map.Entry entry = (Map.Entry)i.next();
                pojo.getRoot().put(entry.getKey().toString(), entry.getValue());
            }
        }

        logger.debug("Finished generating SimulatorPojo from REST content");
        return pojo;
    }

    /**
     * @inheritDoc
     */
    @Override
    protected String getString(SimulatorPojo scriptExecutionResult) throws FormatAdapterException {
        Map<String, Object> pojo  = scriptExecutionResult.getRoot();
        StringBuffer buffer = new StringBuffer();
        buffer.append(pojo.get(PARAM_HTTP_METHOD))
                .append(" - ")
                .append(pojo.get(PARAM_REQUEST_URI));
        if(pojo.entrySet().size() > 2) {
            buffer.append("?");
            Iterator i = pojo.entrySet().iterator();
            while(i.hasNext()) {
                Map.Entry entry = (Map.Entry)i.next();
                if(!(entry.getKey().equals(PARAM_HTTP_METHOD)
                        || entry.getKey().equals(PARAM_REQUEST_URI))) {
                    buffer.append(entry.getKey())
                            .append("=")
                            .append(entry.getValue());
                }
            }
        }
        return buffer.toString();
    }
}
