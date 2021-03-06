package com.tacitknowledge.simulator.formats;

/**
 * Contains the Simulator's default supported format names/acronyms.
 * Use all-capitals.
 * Each supported format will have its corresponding adapter
 *
 * @author Jorge Galindo (jgalindo@tacitknowledge.com)
 */
public final class FormatConstants
{
    /**
     * Constant for JSON format
     */
    public static final String JSON = "JSON";

    /**
     * Constant for XML format
     */
    public static final String XML = "XML";

    /**
     * Constant for CSV format
     */
    public static final String CSV = "CSV";

    /**
     * Constant for YAML format
     */
    public static final String YAML = "YAML";

    /**
     * Constant for DOC_LITERAL_SOAP format
     */
    public static final String DOC_LITERAL_SOAP = "DOCLITERALSOAP";

    /**
     * Constant for YAML format
     */
    public static final String PLAIN_TEXT = "PLAIN TEXT";

    /**
     * Constant for PROPERTIES format
     */
    public static final String PROPERTIES = "PROPERTIES";

    /**
     * Constant for REST format
     */
    public static final String REST = "REST";

    /**
     * Constant for SOAP format
     */
    public static final String SOAP = "SOAP";

    /**
     * Constant for working with full SOAP response format
     */
    public static final String SOAP_FULL_RESPONSE = "SOAPFULLRESPONSE";

    /**
     * Hiding the default constructor
     */
    private FormatConstants()
    {
    }
}
