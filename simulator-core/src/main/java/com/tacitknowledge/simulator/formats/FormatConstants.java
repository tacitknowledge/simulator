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
     * Name for "Post Code Anywhere" format
     */
    public static final String POST_CODE_ANYWHERE = "POSTCODEANYWHERE";

    /**
     * Hiding the default constructor
     */
    private FormatConstants()
    {
    }
}
