package com.tacitknowledge.simulator.transports;

import com.tacitknowledge.simulator.Transport;
import com.tacitknowledge.simulator.TransportException;
import com.tacitknowledge.simulator.configuration.ParameterDefinitionBuilder;
import static com.tacitknowledge.simulator.configuration.ParameterDefinitionBuilder.name;
import static com.tacitknowledge.simulator.configuration.ParametersListBuilder.parameters;
import org.apache.log4j.Logger;

import java.util.List;
import java.util.Map;

/**
 * Transport implementation for File endpoints.
 *
 * @author Jorge Galindo (jgalindo@tacitknowledge.com)
 */
public class FileTransport extends BaseTransport implements Transport
{
    /**
     * Directory name parameter. The underlying directory path to poll from/write to. REQUIRED
     */
    public static final String PARAM_DIRECTORY_NAME = "directoryName";

    /**
     * File name parameter. Name of the file to listen for/write to. OPTIONAL.
     * Mutually exclusive to File Extension & Regex filter.
     * File Name has highest priority if more than one is passed.
     */
    public static final String PARAM_FILE_NAME = "fileName";

    /**
     * File extension parameter. Extension of files the transport will only poll from. OPTIONAL.
     * Mutually exclusive to File Name & Regex filter.
     * File extension has higher priority than Regex filter.
     */
    public static final String PARAM_FILE_EXTENSION = "fileExtension";

    /**
     * Regex filter parameter. Only file name matching the provided regex will be polled. OPTIONAL.
     * Mutually exclusive to File Name & Regex filter.
     * Will be applied only it's the only filter passed.
     */
    public static final String PARAM_REGEX_FILTER = "regexFilter";

    /**
     * Polling interval parameter. Milliseconds before the next poll of the directory. OPTIONAL
     * Defaults to 500 (Camel default)
     */
    public static final String PARAM_POLLING_INTERVAL = "pollingInterval";

    /**
     * Delete file parameter. Determines if file should be deleted after processing. OPTIONAL.
     * Defaults to false. For inbound transports only.
     */
    public static final String PARAM_DELETE_FILE = "deleteFile";

    /**
     * Logger for this class.
     */
    private static Logger logger = Logger.getLogger(FileTransport.class);

    /**
     * Transport parameters definition.
     */
    private List<ParameterDefinitionBuilder.ParameterDefinition> parametersList =
        parameters().
            add(
                name(PARAM_DIRECTORY_NAME).
                    label("Directory Name").
                    required()
            ).
            add(
                name(PARAM_FILE_NAME).
                    label("File Name")
            ).
            add(
                name(PARAM_FILE_EXTENSION).
                    label("File Extension the transport will only poll from " +
                    "(without dot").
                    inOnly()
            ).
            add(
                name(PARAM_REGEX_FILTER).
                    label("Regex filter " +
                    "(will only be applied if neither " +
                    "file name nor extension filters are provided)").
                    inOnly()
            ).
            add(
                name(PARAM_POLLING_INTERVAL).
                    label("Milliseconds before the next poll").
                    inOnly()
            ).
            add(
                name(PARAM_DELETE_FILE).
                    label("Delete file after simulation?").
                    type(ParameterDefinitionBuilder.ParameterDefinition.TYPE_BOOLEAN)
            );

    /**
     * If true, the processed file will be deleted.
     * Defaults to false.
     */
    private boolean deleteFile = false;

    /**
     * @inheritDoc
     */
    public FileTransport()
    {
        super(TransportConstants.FILE);
    }

    /**
     * Used only for inheriting Transports (e.g.: FTP transport)
     *
     * @param type @see #type
     */
    protected FileTransport(String type)
    {
        super(type);
    }

    /**
     * Used only for inheriting Transports
     *
     * @param type       @see #type
     * @param parameters @see #parameters
     */
    protected FileTransport(String type, Map<String, String> parameters)
    {
        super(type, parameters);
    }

    /**
     * Constructor
     *
     * @param parameters @see #parameters
     */
    public FileTransport(Map<String, String> parameters)
    {
        super(TransportConstants.FILE, parameters);
    }

    /**
     * @return @see #Transport.toUriString()
     * @throws TransportException If a required parameter is missing or not properly formatted.
     * @inheritDoc
     */
    public String toUriString() throws TransportException
    {
        validateParameters();

        StringBuilder sb = new StringBuilder("file://");

        // --- directory name
        sb.append(getParamValue(PARAM_DIRECTORY_NAME));

        // --- Options
        StringBuilder options = new StringBuilder();
        if (getParamValue(PARAM_POLLING_INTERVAL) != null)
        {
            options.append("initialDelay=").append(getParamValue(PARAM_POLLING_INTERVAL)).append(AMP);
            options.append("delay=").append(getParamValue(PARAM_POLLING_INTERVAL)).append(AMP);
        }

        if (this.deleteFile)
        {
            options.append("delete=true").append(AMP);
        }

        // --- fileName, fileExtension & Regex filter should be mutually exclusive options
        if (getParamValue(PARAM_FILE_NAME) != null)
        {
            options.append("fileName=").append(getParamValue(PARAM_FILE_NAME));
        }
        else if (getParamValue(PARAM_FILE_EXTENSION) != null)
        {
            // --- File extension is used as a RegEx filter for transport routing
            options.append("include=^.*\\.(").
                append(getParamValue(PARAM_FILE_EXTENSION).toLowerCase()).
                append("|").
                append(getParamValue(PARAM_FILE_EXTENSION).toUpperCase()).
                append(")$");
        }
        else if (getParamValue(PARAM_REGEX_FILTER) != null)
        {
            // --- Regex filter is the last filter to be applied, only if neither of the other 2
            // were provided.
            options.append("include=").append(getParamValue(PARAM_REGEX_FILTER));
        }

        // --- If there are options set, append to the current URI
        if (options.length() > 0)
        {
            sb.append("?").append(options.toString());
        }

        return sb.toString();
    }

    /**
     * @return List of Parameters for File Transport.
     * @inheritDoc
     */
    public List<List> getParametersList()
    {
        return getParametersDefinitionsAsList(parametersList);
    }

    /**
     * @throws TransportException If any required parameter is missing or incorrect
     * @inheritDoc
     */
    @Override
    void validateParameters() throws TransportException
    {
        if (getParamValue(PARAM_DELETE_FILE) != null)
        {
            this.deleteFile = Boolean.parseBoolean(getParamValue(PARAM_DELETE_FILE));
        }

        // ---
        if (getParamValue(PARAM_DIRECTORY_NAME) == null)
        {
            throw new TransportException("Directory Name parameter is required");
        }
    }

    /**
     * @return @see #deleteFile
     */
    protected boolean isDeleteFile()
    {
        return deleteFile;
    }

    /**
     * @param deleteFile @see #deleteFile
     */
    protected void setDeleteFile(boolean deleteFile)
    {
        this.deleteFile = deleteFile;
    }
}
