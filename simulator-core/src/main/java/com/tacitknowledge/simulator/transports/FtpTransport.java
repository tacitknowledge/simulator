package com.tacitknowledge.simulator.transports;

import com.tacitknowledge.simulator.Transport;
import com.tacitknowledge.simulator.TransportException;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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
     * Username parameter. Username to login as. OPTIONAL.
     * If not provided, anonymous login will be attempted.
     */
    public static final String PARAM_USERNAME = "username";

    /**
     * Password parameter. Used to login to the remote file system. OPTIONAL.
     */
    public static final String PARAM_PASSWORD = "password";

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
     * Transport parameters definition.
     */
    private List<List> parametersList = new ArrayList<List>()
    {
        {

            add(new ArrayList<String>()
            {
                {
                    add(PARAM_HOST);
                    add("Host Name");
                    add("string");
                    add("required");
                }
            });

            add(new ArrayList<String>()
            {
                {
                    add(PARAM_SFTP);
                    add("Is this an SFTP transport? (defaults to FTP)");
                    add("boolean");
                    add("optional");
                }
            });

            add(new ArrayList<String>()
            {
                {
                    add(PARAM_PORT);
                    add("Port (defaults to 21 for FTP and 22 for SFTP)");
                    add("string");
                    add("optional");
                }
            });

            add(new ArrayList<String>()
            {
                {
                    add(PARAM_DIRECTORY_NAME);
                    add("Directory Name");
                    add("string");
                    add("optional");
                }
            });

            add(new ArrayList<String>()
            {
                {
                    add(PARAM_USERNAME);
                    add("User Name");
                    add("string");
                    add("optional");
                }
            });

            add(new ArrayList<String>()
            {
                {
                    add(PARAM_PASSWORD);
                    add("Password");
                    add("string");
                    add("optional");
                }
            });

            add(new ArrayList<String>()
            {
                {
                    add(PARAM_FILE_NAME);
                    add("File Name (file name the transport will only poll from)");
                    add("string");
                    add("optional");
                }
            });

            add(new ArrayList<String>()
            {
                {
                    add(PARAM_FILE_EXTENSION);
                    add("File Extension the transport will only poll from (without dot)");
                    add("string");
                    add("optional");
                }
            });

            add(new ArrayList<String>()
            {
                {
                    add(PARAM_REGEX_FILTER);
                    add("Regex filter " +
                            "(will only be applied if neither " +
                            "file name nor extension filters are provided)");
                    add("string");
                    add("optional");
                }
            });

            add(new ArrayList<String>()
            {
                {
                    add(PARAM_DELETE_FILE);
                    add("Delete file after simulation? (defaults to NO)");
                    add("boolean");
                    add("optional");
                }
            });

            add(new ArrayList<String>()
            {
                {
                    add(PARAM_POLLING_INTERVAL);
                    add("Milliseconds before the next poll (defaults to 500)");
                    add("string");
                    add("optional");
                }
            });

            add(new ArrayList<String>()
            {
                {
                    add(PARAM_BINARY);
                    add("Is file transfer binary? (defaults to NO)");
                    add("boolean");
                    add("optional");
                }
            });
        }
    };

    /**
     * Flag to determine if this transport is FTP or SFTP. Defaults to FTP
     */
    private boolean sftp;

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
     * @inheritDoc
     */
    public FtpTransport(Map<String, String> parameters)
    {
        super(TransportConstants.FTP, parameters);
    }

    /**
     * @return @see #Transport.toUriString()
     * @throws TransportException If a required parameter is missing or not properly formatted.
     * @inheritDoc
     */
    @Override
    public String toUriString() throws TransportException
    {
        validateParameters();

        StringBuilder sb = new StringBuilder();

        // --- Check the protocol
        if (this.sftp)
        {
            sb.append("sftp");
        }
        else
        {
            sb.append("ftp");
        }
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

        // --- Append the options...
        sb.append("?");
        // --- If we have password
        if (getParamValue(PARAM_PASSWORD) != null)
        {
            sb.append("password=").append(getParamValue(PARAM_PASSWORD)).append(AMP);
        }

        // --- fileName, fileExtension & Regex filter should be mutually exclusive options.
        // fileName takes priority, Regex filter having the lowest.
        if (getParamValue(PARAM_FILE_NAME) != null)
        {
            sb.append("fileName=").append(getParamValue(PARAM_FILE_NAME)).append(AMP);
        }
        else if (getParamValue(PARAM_FILE_EXTENSION) != null)
        {
            // --- File extension is used as a RegEx filter for transport routing
            sb.append("include=^.*(i)(.").append(getParamValue(PARAM_FILE_EXTENSION)).append(")");
            sb.append(AMP);
        }
        else if (getParamValue(PARAM_REGEX_FILTER) != null)
        {
            // --- Regex filter is the last filter to be applied, only if neither of the other 2
            // were provided.
            sb.append("include=").append(getParamValue(PARAM_REGEX_FILTER)).append(AMP);
        }

        // ---
        if (isDeleteFile())
        {
            sb.append("delete=true").append(AMP);
        }

        if (getParamValue(PARAM_POLLING_INTERVAL) != null)
        {
            sb.append("initialDelay=").append(getParamValue(PARAM_POLLING_INTERVAL)).append(AMP);
            sb.append("delay=").append(getParamValue(PARAM_POLLING_INTERVAL)).append(AMP);
        }

        // --- If file transfer is binary
        if (this.binary)
        {
            sb.append("binary=true");
        }

        return sb.toString();
    }

    /**
     * @return List of Parameters for Ftp Transport.
     * @inheritDoc
     */
    @Override
    public List<List> getParametersList()
    {
        return this.parametersList;
    }

    /**
     * @inheritDoc
     * @throws TransportException If any required parameter is missing or incorrect
     */
    @Override
    void validateParameters() throws TransportException
    {
        // --- If passed, assign the boolean parameters to instance variables
        if (getParamValue(PARAM_SFTP) != null)
        {
            this.sftp = Boolean.parseBoolean(getParamValue(PARAM_SFTP));
        }
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
            throw new TransportException("Host name parameter is required");
        }
    }
}
