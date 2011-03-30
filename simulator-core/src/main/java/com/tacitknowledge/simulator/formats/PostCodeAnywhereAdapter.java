package com.tacitknowledge.simulator.formats;

import com.tacitknowledge.simulator.ConfigurableException;

import java.util.Map;

/**
 * This adapter is used to handle "Post Code Anywhere" format which is basically a HTTP request
 * like
 *
 * http://services.postcodeanywhere.co.uk/PostcodeAnywhere/Interactive/RetrieveByAddress/v1.20/json.ws?Key=WG39-YN16-GH19-RY96&amp;Address=TW9+1EP
 *
 * and returns a JSON string
 *
 * For details: http://www.postcodeanywhere.co.uk/support/webservices/PostcodeAnywhere/Interactive/Find/v1.1/default.aspx
 *
 * @author Adrian Neaga (aneaga@tacitknowledge.com)
 */
public class PostCodeAnywhereAdapter extends AbstractHttpAdapter
{

    /** Top level name used in scenarios */
    public static final String POST_CODE_REQUEST = "postcoderequest";


    /**
     * Constructor
     * @param bound specifies if adapter is inbound or outbound
     * @param parameters base parameters for this adapter
     */
    public PostCodeAnywhereAdapter(final int bound, final Map<String, String> parameters)
    {
        super(bound, parameters);
    }

    /**
     * Constructor
     */
    public PostCodeAnywhereAdapter()
    {
        super();
    }

    @Override
    public String getRootBeanName()
    {
        return POST_CODE_REQUEST;
    }

    @Override
    protected void validateParameters() throws ConfigurableException
    {
    }
}
