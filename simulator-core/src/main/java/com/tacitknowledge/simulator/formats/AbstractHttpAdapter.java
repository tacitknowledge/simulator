package com.tacitknowledge.simulator.formats;

import com.tacitknowledge.simulator.FormatAdapterException;
import com.tacitknowledge.simulator.SimulatorPojo;
import com.tacitknowledge.simulator.StructuredSimulatorPojo;
import org.apache.camel.Exchange;

import javax.servlet.http.HttpServletRequest;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

/**
 * This is a generic adapter that can be used to handle any HTTP request.
 *
 * In the generated bean, it puts all the request parameters with their values so you can reference
 * them in the scenarios.
 *
 * For example, with a request like: http://localhost:8080/Resource?id=12&name=something
 *
 * in the scenario, you can reference both the id and name like:
 *
 * [when] request.id == '12' && request.name == 'something' [execute] request.response='heres the
 * response'
 *
 * 'request' is the name which <code>getRootBeanName</code> needs to return
 *
 * @author Adrian Neaga (aneaga@tacitknowledge.com)
 */
public abstract class AbstractHttpAdapter extends BaseAdapter
{
    /** Name for response property of the Pojo */
    public static final String RESPONSE = "response";

    /**
     * Constructor
     *
     * @param bound      specifies if adapter is inbound or outbound
     * @param parameters base parameters for this adapter
     */
    public AbstractHttpAdapter(final int bound, final Map<String, String> parameters)
    {
        super(bound, parameters);
    }

    /** Constructor */
    public AbstractHttpAdapter()
    {
        super();
    }

    @Override
    protected SimulatorPojo createSimulatorPojo(Exchange exchange) throws FormatAdapterException
    {
        SimulatorPojo pojo = new StructuredSimulatorPojo();

        HttpServletRequest request = exchange.getIn().getBody(HttpServletRequest.class);

        Map<String, String> requestParameters = createMapWithRequestParameters(request);

        // This is to make the resulting bean have a property called 'response'
        requestParameters.put(RESPONSE, "");

        pojo.getRoot().put(getRootBeanName(), requestParameters);

        return pojo;
    }

    /**
     * Creates a map of paramName=>value pairs.
     *
     * @param pojo    pojo
     * @param request http request
     * @return map of params with values
     */
    protected Map<String, String> createMapWithRequestParameters(HttpServletRequest request)
    {
        Map<String, String> parameters = new HashMap<String, String>();

        Enumeration enumer = request.getParameterNames();
        while (enumer.hasMoreElements())
        {
            String element = (String) enumer.nextElement();
            parameters.put(element, request.getParameter(element));
        }

        return parameters;
    }

    @Override
    protected String getString(SimulatorPojo scriptExecutionResult, Exchange exchange)
        throws FormatAdapterException
    {
        String response = null;

        Map postCodeData = (Map) scriptExecutionResult.getRoot().get(getRootBeanName());

        if (postCodeData != null)
        {
            response = (String) postCodeData.get(RESPONSE);
        }

        return response;
    }

    /**
     * This is meant to return the name that will be used to reference the bean in scenario
     * scripts.
     *
     * @return name
     */
    public abstract String getRootBeanName();

}
