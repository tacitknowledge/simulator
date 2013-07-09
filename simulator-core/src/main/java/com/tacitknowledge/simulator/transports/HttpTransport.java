package com.tacitknowledge.simulator.transports;


import com.tacitknowledge.simulator.Configurable;
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
     * @param configurable @see Configurable
     */
    public HttpTransport(final Configurable configurable)
    {
        this(null,configurable);
    }


    /**
     * Constructor.
     *
     * @param type       - transport type
     * @param configurable @see Configurable
     */
    public HttpTransport(final String type, final Configurable configurable)
    {
        super(type, configurable);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void validateParameters() throws ConfigurableException
    {

        if (!this.isHttpOut() && configurable.getParamValue(PARAM_RESOURCE_URI) == null
                && configurable.getBound() == Configurable.BOUND_OUT)
        {
            throw new ConfigurableException("Resource URI parameter is required");
        }
        if (configurable.getParamValue(PARAM_RESOURCE_URI) == null
                && configurable.getBound() == Configurable.BOUND_IN)
        {
            throw new ConfigurableException("Resource URI parameter is required ["
                    + (configurable.getBound() == Configurable.BOUND_IN)
                    + ","
                    + configurable.getParamValue(PARAM_RESOURCE_URI) );
        }

        if(!isSSLCompletelySpecifiedOrNotAtAll()){
            throw new ConfigurableException( "Ssl requires all of the following parameters: " + 
                    KEY_STORE_FILE + ", " + KEY_PASSWORD + ", " + STORE_PASSWORD);
        }

    }

    private boolean isStorePasswordSpecified() {
        return configurable.getParamValue(STORE_PASSWORD)!=null;
    }

    private boolean isKeyStoreFileSpecified() {
        return configurable.getParamValue(KEY_STORE_FILE) != null;
    }

    private boolean isKeyPasswordSpecified() {
        return configurable.getParamValue(KEY_PASSWORD) != null;
    }

    private boolean isSSL() {
        return (configurable.getParamValue(isSSL) != null)
            ? Boolean.parseBoolean(configurable.getParamValue(isSSL))
            : false;
    }

    private boolean isSSLCompletelySpecifiedOrNotAtAll() {
        return (
                (isSSL() && isKeyPasswordSpecified() && isKeyStoreFileSpecified() && isStorePasswordSpecified())
                ||
                (!isSSL() && !isKeyPasswordSpecified() && !isKeyStoreFileSpecified() && !isStorePasswordSpecified())

        );

    }

    protected boolean isHttpOut() {
        if (configurable.getParamValue(PARAM_HTTP_OUT) != null)
        {
            return Boolean.parseBoolean(configurable.getParamValue(PARAM_HTTP_OUT));
        }
        return false;

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getUriString() throws ConfigurableException, TransportException
    {
        // --- If this transport is an HTTP OUT, we just end the Camel route,
        // so we return the result from the execution script as the HTTP response body
        if (isHttpOut())
        {
            return MOCK_RESULT;
        }

        // --- 
        StringBuilder sb = new StringBuilder();
        if(isSSL()){
            sb.append("jetty:https://");
            setSSLSystemProperties();
        }
        else
        {
            sb.append("jetty:http://");
        }     
        
        sb.append(HOST);

        if (configurable.getParamValue(PARAM_PORT) != null)
        {
            sb.append(":").append(configurable.getParamValue(PARAM_PORT));
        }

        sb.append(configurable.getParamValue(PARAM_RESOURCE_URI));
        
        sb.append("?matchOnUriPrefix=true");



        logger.info("Uri String: {}", sb.toString());

        return sb.toString();
    }

    private void setSSLSystemProperties() {
        // ssl parameters are set as system parameters
        //todo - mws - eclipse driven jetty only?
        System.setProperty("org.eclipse.jetty.ssl.keypassword", configurable.getParamValue(KEY_PASSWORD));
        System.setProperty("org.eclipse.jetty.ssl.keystore", configurable.getParamValue(KEY_STORE_FILE));
        System.setProperty("org.eclipse.jetty.ssl.password", configurable.getParamValue(STORE_PASSWORD));
    }

}
