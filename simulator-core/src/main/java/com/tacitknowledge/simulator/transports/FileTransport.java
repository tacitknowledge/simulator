package com.tacitknowledge.simulator.transports;

import java.util.HashMap;
import java.util.Map;

import com.tacitknowledge.simulator.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
    private static Logger logger = LoggerFactory.getLogger(FileTransport.class);


    /**
     * @inheritDoc
     */
    public FileTransport()
    {
        this(TransportConstants.FILE);
    }

    /**
     * Used only for inheriting Transports (e.g.: FTP transport)
     *
     * @param type @see #type
     */
    public FileTransport(final String type)
    {
        this(type, new BaseConfigurable(Configurable.BOUND_IN,new HashMap<String, String>()));
    }

    /**
     * Contructor that initialize the File Transport with parameters
     * @param configurable - transport parameters
     */
    public FileTransport(Configurable configurable)
    {
        this(TransportConstants.FILE, configurable);
    }

    /**
     * Used only for inheriting Transports
     *
     * @param type       @see #type
     * @param configurable @see #configurable
     */
    protected FileTransport(final String type,final Configurable configurable)
    {
        super(type, configurable);
    }

    /**
     * {@inheritDoc}
     */
    protected String getUriString() throws ConfigurableException, TransportException
    {
        StringBuilder sb = new StringBuilder("file://");

        // --- directory name
        sb.append(configurable.getParamValue(PARAM_DIRECTORY_NAME));

        // --- Options
        StringBuilder options = new StringBuilder();
        if (configurable.getParamValue(PARAM_POLLING_INTERVAL) != null)
        {
            options.append("initialDelay=").append(
                    configurable.getParamValue(PARAM_POLLING_INTERVAL)).append(AMP);
            options.append("delay=").append(
                    configurable.getParamValue(PARAM_POLLING_INTERVAL)).append(AMP);
        }

        if (this.isDeleteFile())
        {
            options.append("delete=true").append(AMP);
        }

        // --- fileName, fileExtension & Regex filter should be mutually exclusive options
        if (configurable.getParamValue(PARAM_FILE_NAME) != null)
        {
            options.append("fileName=").append(configurable.getParamValue(PARAM_FILE_NAME));
        }
        else if (configurable.getParamValue(PARAM_FILE_EXTENSION) != null)
        {
            // --- File extension is used as a RegEx filter for transport routing
            options.append("include=^.*\\.(").
                append(configurable.getParamValue(PARAM_FILE_EXTENSION).toLowerCase()).
                append("|").
                append(configurable.getParamValue(PARAM_FILE_EXTENSION).toUpperCase()).
                append(")$");
        }
        else if (configurable.getParamValue(PARAM_REGEX_FILTER) != null)
        {
            // --- Regex filter is the last filter to be applied, only if neither of the other 2
            // were provided.
            options.append("include=").append(configurable.getParamValue(PARAM_REGEX_FILTER));
        }

        // --- If there are options set, append to the current URI
        if (options.length() > 0)
        {
            sb.append("?").append(options.toString());
        }

        logger.info("Uri String: {}", sb.toString());

        return sb.toString();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void validateParameters() throws ConfigurableException
    {
        if (configurable.getParamValue(PARAM_DIRECTORY_NAME) == null)
        {
            throw new ConfigurableException("Directory Name parameter is required");
        }
    }

    /**
     * @return @see #deleteFile
     */
    protected boolean isDeleteFile()
    {
        return (configurable.getParamValue(PARAM_DELETE_FILE) != null)
                ? Boolean.parseBoolean(configurable.getParamValue(PARAM_DELETE_FILE))
                : false;
    }
}
