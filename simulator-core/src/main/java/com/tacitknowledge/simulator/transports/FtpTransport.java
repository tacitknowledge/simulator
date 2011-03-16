package com.tacitknowledge.simulator.transports;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tacitknowledge.simulator.ConfigurableException;
import com.tacitknowledge.simulator.Transport;
import com.tacitknowledge.simulator.TransportException;

/**
 * Transport implementation for FTP/SFTP endpoints.
 * FTP transport share File transport options, so it will inherit from FileTransport
 *
 * @author Jorge Galindo (jgalindo@tacitknowledge.com)
 */
public class FtpTransport extends FileTransport implements Transport
{
    /**
     * FTP/SFTP host name parameter. REQUIRED.
     */
    public static final String PARAM_HOST = "host";

    /**
     * FTP/SFTP port parameter. OPTIONAL.
     * Defaults to 21 for FTP and 22 for SFTP.
     */
    public static final String PARAM_PORT = "port";

    /**
     * SFTP parameter. Determines if this transport is SFTP (true) or FTP (false). OPTIONAL.
     * Defaults to false (FTP)
     */
    public static final String PARAM_SFTP = "sftp";

    /**
     * Binary parameter. Determines transfer mode, BINARY (true) or ASCII (false). OPTIONAL.
     * Defaults to false (ASCII)
     */
    public static final String PARAM_BINARY = "binary";

    /**
     * Logger for this class.
     */
    private static Logger logger = LoggerFactory.getLogger(FtpTransport.class);

    /**
     * Flag to determine the file transfer mode, BINARY or ASCII. Defaults to ASCII
     */
    private boolean binary;

    /**
     * @inheritDoc
     */
    public FtpTransport()
    {
        super(TransportConstants.FTP);
    }

    /**
     * @param parameters @see #parameters
     * {@inheritDoc}
     */
    public FtpTransport(final int bound, final Map<String, String> parameters)
    {
        super(bound, TransportConstants.FTP, parameters);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected String getUriString() throws ConfigurableException, TransportException
    {
        return getUriString(TransportConstants.FTP.toLowerCase());
    }

    /**
     * This method will be used by SftpTransport and FtpsTransport.
     */
    protected String getUriString(String protocol) throws ConfigurableException, TransportException
    {
        StringBuilder sb = new StringBuilder();

        // --- Check the protocol
        sb.append(protocol);
        sb.append("://");

        // --- If we have username...
        if (getParamValue(PARAM_USERNAME) != null)
        {
            sb.append(getParamValue(PARAM_USERNAME)).append("@");
        }

        // ---
        sb.append(getParamValue(PARAM_HOST));

        // --- If we have port
        if (getParamValue(PARAM_PORT) != null)
        {
            sb.append(":").append(getParamValue(PARAM_PORT));
        }

        // --- If we have directory name
        if (getParamValue(PARAM_DIRECTORY_NAME) != null)
        {
            sb.append("/").append(getParamValue(PARAM_DIRECTORY_NAME));
        }

        // --- Options...
        StringBuilder options = new StringBuilder();
        // --- If we have password
        if (getParamValue(PARAM_PASSWORD) != null)
        {
            options.append("password=").append(getParamValue(PARAM_PASSWORD)).append(AMP);
        }

        // ---
        if (isDeleteFile())
        {
            options.append("delete=true").append(AMP);
        }

        if (getParamValue(PARAM_POLLING_INTERVAL) != null)
        {
            options.append("initialDelay=").append(getParamValue(PARAM_POLLING_INTERVAL))
                    .append(AMP);
            options.append("delay=").append(getParamValue(PARAM_POLLING_INTERVAL)).append(AMP);
        }

        // --- If file transfer is binary
        if (this.binary)
        {
            options.append("binary=true").append(AMP);
        }

        // --- fileName, fileExtension & Regex filter should be mutually exclusive options.
        // fileName takes priority, Regex filter having the lowest.
        if (getParamValue(PARAM_FILE_NAME) != null)
        {
            options.append("fileName=").append(getParamValue(PARAM_FILE_NAME));
        }
        else if (getParamValue(PARAM_FILE_EXTENSION) != null)
        {
            // --- File extension is used as a RegEx filter for transport routing
            options.append("include=^.*\\.(")
                    .append(getParamValue(PARAM_FILE_EXTENSION).toLowerCase()).append("|")
                    .append(getParamValue(PARAM_FILE_EXTENSION).toUpperCase()).append(")$");
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

        logger.info("Uri String: {}", sb.toString());

        return sb.toString();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void validateParameters() throws ConfigurableException
    {
        // --- If passed, assign the boolean parameters to instance variables
        if (getParamValue(PARAM_BINARY) != null)
        {
            this.binary = Boolean.parseBoolean(getParamValue(PARAM_BINARY));
        }
        if (getParamValue(PARAM_DELETE_FILE) != null)
        {
            setDeleteFile(Boolean.parseBoolean(getParamValue(PARAM_DELETE_FILE)));
        }

        if (getParamValue(PARAM_HOST) == null)
        {
            throw new ConfigurableException("Host name parameter is required");
        }
    }
}
