package com.tacitknowledge.simulator.formats;

import java.io.IOException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.tacitknowledge.simulator.*;
import org.apache.bsf.BSFException;
import org.apache.bsf.BSFManager;
import org.apache.camel.Exchange;
//import org.codehaus.jackson.map.ObjectMapper;
import org.mozilla.javascript.NativeObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Implementation of the Adapter interface for the REST format
 *
 * @author Raul Huerta (rhuerta@tacitknowledge.com)
 */

public class RestAdapter extends NativeObjectScriptingAdapter implements Adapter
{

    /**
     * property extraction pattern
     */
    public static final String PARAM_EXTRACTION_PATTERN = "extractionPattern";
    /**
     * property object name
     */
    public static final String PARAM_OBJECT_NAME = "objectName";
    /**
     * response property
     */
    public static final String RESPONSE = "response";
    /**
     * request property
     */
    public static final String REQUEST = "request";
    /**
     * body of the request.
     */
    public static final String BODY = "body";
    /**
     * content type for the response.
     */
    public static final String CONTENT_TYPE = "contentType";
    /**
     * response code.
     */
    public static final String STATUS_CODE = "statusCode";
    /**
     * this is the default value for the status code.
     */
    public static final String DEFAULT_STATUS_CODE = "200";
    /**
     * used for spliting the url.
     */
    public static final String DEFAULT_EXTRACTION_PATTERN = "/";
    /**
     * default content type.
     */
    public static final String HTML_CONTENT_TYPE = "text/html";
    /**
     * default object name available in the scenario scripts.
     */
    public static final String DEFAULT_OBJECT_NAME = "obj";
    /**
     * Logger for this class.
     */
    private static Logger logger = LoggerFactory.getLogger(RestAdapter.class);
    /**
     * params property of the request
     */
    private static final String PARAMS = "params";
    /**
     * headers property of the request
     */
    private static final String HEADERS = "headers";
    /**
     * method property of the request
     */
    private static final String METHOD = "method";

    /**
     * @inheritDoc
     */
    public RestAdapter()
    {
        super();
    }


    public RestAdapter(Configurable configurable) {
        super(configurable);
    }

    /**
     * @inheritDoc
     * @throws ConfigurableException If any required parameter has not been set.
     *
     */

    public void validateParameters() throws ConfigurableException
    {

    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Override
    protected SimulatorPojo createSimulatorPojo(final Exchange exchange)
        throws FormatAdapterException
    {

        logger.debug("Attempting to generate SimulatorPojo from REST content:\n{}", exchange);

        HttpServletRequest request = exchange.getIn().getBody(HttpServletRequest.class);

        SimulatorPojo pojo = new StructuredSimulatorPojo();

        Map attributes = new HashMap<String, Object>();
        String parameterExtractionPattern =
                configuration.getParamValue(PARAM_EXTRACTION_PATTERN) == null
                        ? DEFAULT_EXTRACTION_PATTERN
                        : configuration.getParamValue(PARAM_EXTRACTION_PATTERN);
        attributes.put(REQUEST, populateRequestAttributes(request, parameterExtractionPattern));
        attributes.put(RESPONSE, populateResponseAttributes());

        String parameterObjectName =
                configuration.getParamValue(PARAM_OBJECT_NAME) == null
                        ? DEFAULT_OBJECT_NAME
                        : configuration.getParamValue(PARAM_OBJECT_NAME);
        pojo.getRoot().put(parameterObjectName, attributes);

        logger.debug("Finished generating SimulatorPojo from REST content");

        return pojo;
    }

    /**
     * Populate a map with default response values for status code, content type and response body
     *
     * @return Map of String, Object entries
     */
    private Map<String, Object> populateResponseAttributes()
    {
        Map<String, Object> responseMap = new HashMap<String, Object>();
        responseMap.put(STATUS_CODE, DEFAULT_STATUS_CODE);
        responseMap.put(CONTENT_TYPE, HTML_CONTENT_TYPE);
        responseMap.put(BODY, "");
        
        return responseMap;
    }

    /**
     * Populate a map with values coming from the HTTP request plus the ones coming from the
     * REST request.
     *
     * @param request           - HttpServlet Request
     * @param extractionPattern - Pattern used to parse REST URL
     * @return Map of String, Object entries
     */
    @SuppressWarnings("rawtypes")
    private Map<String, Object> populateRequestAttributes(
            final HttpServletRequest request,
            final String extractionPattern)
    {
        Map<String, Object> requestMap = new HashMap<String, Object>();

        Map<String, Object> paramMap = new HashMap<String, Object>();
        Map<String, Object> headersMap = new HashMap<String, Object>();
        if (request != null)
        {

            requestMap.put(METHOD, request.getMethod());

            Enumeration enumer = request.getParameterNames();
            while (enumer.hasMoreElements())
            {
                String element = (String) enumer.nextElement();
                paramMap.put(element, request.getParameter(element));
            }

            enumer = request.getHeaderNames();
            while (enumer.hasMoreElements())
            {
                String header = (String) enumer.nextElement();
                headersMap.put(header, request.getHeader(header));
            }

            Map<String, Object> values =
                    extractValuesFromUri(request.getRequestURI(), extractionPattern);

            if (null != values && !values.isEmpty())
            {
                paramMap.putAll(values);
            }
        }
        requestMap.put(PARAMS, paramMap);
        requestMap.put(HEADERS, headersMap);

        return requestMap;
    }

    /**
     * @inheritDoc
     *
     * @param scriptExecutionResult SimulatorPojo with structured data from the execution script
     * @param exchange The Camel Exchange
     * @return The String representation of the input data
     * @throws FormatAdapterException If any error occurs
     */
    @SuppressWarnings("unchecked")
    @Override
    protected String getConversationResponseAsString(final SimulatorPojo scriptExecutionResult, final Exchange exchange)
        throws FormatAdapterException
    {

        HttpServletResponse response;
        String body = "";
        //get the first entry
        Map<String, Object> pojo = null;
        if (scriptExecutionResult.getRoot() != null)
        {
            for (Map.Entry<String, Object> entry : scriptExecutionResult.getRoot().entrySet())
            {
                pojo = (Map<String, Object>) entry.getValue();
                break;
            }
        }

        if (pojo != null && pojo.size() > 0)
        {
            Map<String, Object> responseMap = (Map<String, Object>) pojo.get(RESPONSE);
            body = (String) responseMap.get(BODY);
            String contentType = (String) responseMap.get(CONTENT_TYPE);
            String statusCode = (String) responseMap.get(STATUS_CODE);

            response = exchange.getIn().getBody(HttpServletResponse.class);

            response.setContentType(contentType);
            response.setStatus(Integer.parseInt(statusCode));
        }
        return body;
    }

    /**
     * Extracts information from the URL given a REST pattern
     *
     * @param url     URL
     * @param pattern pattern to use
     * @return Map with parameters read from the URL
     */
    private Map<String, Object> extractValuesFromUri(final String url, final String pattern)
    {
        String[] patternArr = pattern.split(DEFAULT_EXTRACTION_PATTERN);
        Object[] urlArr = url.split(DEFAULT_EXTRACTION_PATTERN);
        Map<String, Object> parameterMap = new HashMap<String, Object>();

        int patternArrLength = patternArr.length;
        for (int i = 0; i < patternArrLength; i++)
        {
            String str = patternArr[i];
            if (str.startsWith(":"))
            {
                try
                {
                    String value = (String) urlArr[i];
                    if (value.contains("."))
                    {
                        String[] arr = value.split("\\.");
                        value = arr[0];
                    }
                    parameterMap.put(str.substring(1), value);
                }
                catch (ArrayIndexOutOfBoundsException e)
                {
                    //This is fine, since it means that the url is shorter that the pattern.
                    //We just swallow the exception and continue.
                }
            }
        }
        return parameterMap;
    }
}
