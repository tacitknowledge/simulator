package com.tacitknowledge.simulator.transports;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tacitknowledge.simulator.ConfigurableException;
import com.tacitknowledge.simulator.Transport;
import com.tacitknowledge.simulator.TransportException;

/**
 * @author galo
 */
public class HttpTransport extends BaseTransport implements Transport
{
    public static final String MOCK_RESULT = "mock:result";
    /**
     * Resource URI parameter. REQUIRED
     */
    public static final String PARAM_RESOURCE_URI = "resourceURI";
    /**
     * HTTP out parameter.
     */
    public static final String PARAM_HTTP_OUT = "httpOut";
    /**
     * We use 0.0.0.0 instead of localhost to receive requests from any host.
     */
    private static final String HOST = "0.0.0.0";
    /**
     * http over ssl parameter
     */
    public static final String isSSL = "isSSL";
    /**
     * Path to the file containing X.509 certificate.
     */
    public static final String KEY_STORE_FILE = "keyStoreFile";
    /**
     * Store password.
     */
    public static final String STORE_PASSWORD = "storePassword";
    /**
     * Key password.
     */
    public static final String KEY_PASSWORD = "keyPassword";
    /**
     * Logger for this class.
     */
    private static Logger logger = LoggerFactory.getLogger(HttpTransport.class);

    /**
     * Checks if http transport is used for output
     */
    private boolean isHttpOut = false;

    /**
     * Constructor
     * This constructor should only be called from the inheriting Transports.
     *
     * @param type - Transport type
     */
    public HttpTransport(final String type)
    {
        super(type);
    }

    /**
     * Constructor.
     *
     * @param bound      - specifies if transport is for in or out
     * @param type       - transport type
     * @param parameters @see #parameters
     */
    public HttpTransport(final int bound, final String type,
                            final Map<String, String> parameters)
    {
        super(bound, type, parameters);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void validateParameters() throws ConfigurableException
    {
        if (getParamValue(PARAM_HTTP_OUT) != null)
        {
            this.isHttpOut = Boolean.parseBoolean(getParamValue(PARAM_HTTP_OUT));
        }

        if (!this.isHttpOut && getParamValue(PARAM_RESOURCE_URI) == null)
        {
            throw new ConfigurableException("Resource URI parameter is required");
        }
        int count = 0;
        boolean ssl = false;
        if(Boolean.parseBoolean(getParamValue(isSSL))){
            ssl=true;
            count++;
        }
        
        if(getParamValue(KEY_PASSWORD)!=null){
            count++;
        }
        
        if(getParamValue(KEY_STORE_FILE)!=null){
            count++;
        }
        
        if(getParamValue(STORE_PASSWORD)!=null){
            count++;
        }
        
        if(ssl && count!=4){
            throw new ConfigurableException( "Ssl requires all of the following parameters: " + 
                    KEY_STORE_FILE + ", " + KEY_PASSWORD + ", " + STORE_PASSWORD);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getUriString() throws ConfigurableException, TransportException
    {
        // --- If this transport is an HTTP OUT, we just end the Camel route,
        // so we return the result from the execution script as the HTTP response body
        if (this.isHttpOut)
        {
            return MOCK_RESULT;
        }

        // --- 
        StringBuilder sb = new StringBuilder();
        if(Boolean.parseBoolean(getParamValue(isSSL))){
            sb.append("jetty:https://");
        }
        else
        {
            sb.append("jetty:http://");
        }     
        
        sb.append(HOST);

        if (getParamValue(PARAM_PORT) != null)
        {
            sb.append(":").append(getParamValue(PARAM_PORT));
        }

        sb.append(getParamValue(PARAM_RESOURCE_URI));
        
        sb.append("?matchOnUriPrefix=true");
        
        // ssl parameters are set as system parameters 
        if(getParamValue(KEY_PASSWORD)!=null){
            System.setProperty("org.eclipse.jetty.ssl.keypassword", getParamValue(KEY_PASSWORD));
        }
        
        if(getParamValue(KEY_STORE_FILE)!=null){
            System.setProperty("org.eclipse.jetty.ssl.keystore", getParamValue(KEY_STORE_FILE));
        }
        
        if(getParamValue(STORE_PASSWORD)!=null){
            System.setProperty("org.eclipse.jetty.ssl.password", getParamValue(STORE_PASSWORD));
        }
        
        logger.info("Uri String: {}", sb.toString());

        return sb.toString();
    }
    
}
