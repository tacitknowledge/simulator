package com.tacitknowledge.simulator.transports;

import java.lang.annotation.Inherited;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tacitknowledge.simulator.ConfigurableException;
import com.tacitknowledge.simulator.TransportException;

public class FtpsTransport extends FtpTransport
{

    public static final String FTP_CLIENT_KEY_STORE_KEY_PASSWORD = "ftpClient.keyStore.keyPassword=";

    public static final String FTP_CLIENT_KEY_STORE_PASSWORD = "ftpClient.keyStore.password=";

    public static final String FTP_CLIENT_KEY_STORE_FILE = "ftpClient.keyStore.file=";

    public static final String REQUIRED_PARAMETERS_MESSAGE = "For active mode all of the following parameters are required: ";

    /**
     * Logger for this class.
     */
    private static Logger logger = LoggerFactory.getLogger(FtpsTransport.class);

    /**
     * file path for certificate
     */
    private String keyFile;

    /**
     * key password
     */
    private String keyPassword;

    /**
     * private key password
     */
    private String privateKeyPassword;

    /**
     *  Key Store file.
     */
    public static final String KEY_STORE_FILE = "keyStoreFile";

    /**
     * Key password.
     */
    public static final String KEY_STORE_PASSWORD = "keyStorePassword";

    /**
     * Private key password.
     */
    public static final String KEY_STORE_KEY_PASSWORD = "keyStoreKeyPassword";

    /**
     * {@link Inherited}
     */
    FtpsTransport()
    {
        super();
    }

    /**
     * 
     * @param bound Inbound or Outbound
     * @param parameters - transport parameters
     */
    public FtpsTransport(final int bound, final Map<String, String> parameters)
    {
        super(bound, parameters);
    }

    /**
     * This method will be used by SftpTransport and FtpsTransport.
     */
    public String getUriString() throws ConfigurableException, TransportException
    {
        StringBuilder sb = new StringBuilder();

        // --- Check the protocol
        sb.append(TransportConstants.FTPS.toLowerCase());
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
        if (isBinary())
        {
            options.append("binary=true").append(AMP);
        }

        // next three parameters are necessary for active mode ftps 
        if (getParamValue(KEY_STORE_FILE) != null)
        {
            options.append(FTP_CLIENT_KEY_STORE_FILE + getParamValue(KEY_STORE_FILE)).append(AMP);
        }
        if (getParamValue(KEY_STORE_PASSWORD) != null)
        {
            options.append(FTP_CLIENT_KEY_STORE_PASSWORD + getParamValue(KEY_STORE_PASSWORD))
                    .append(AMP);
        }
        if (getParamValue(KEY_STORE_KEY_PASSWORD) != null)
        {
            options.append(
                    FTP_CLIENT_KEY_STORE_KEY_PASSWORD + getParamValue(KEY_STORE_KEY_PASSWORD))
                    .append(AMP);
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
    public void validateParameters() throws ConfigurableException
    {
        boolean activeMode = false;
        int count = 0;
        // --- If passed, assign the boolean parameters to instance variables
        if (getParamValue(KEY_STORE_FILE) != null)
        {
            activeMode = true;
            ++count;
        }

        if (getParamValue(KEY_STORE_PASSWORD) != null)
        {
            activeMode = true;
            ++count;
        }

        if (getParamValue(KEY_STORE_KEY_PASSWORD) != null)
        {
            activeMode = true;
            ++count;
        }

        if (activeMode && count != 3)
        {
            throw new ConfigurableException(REQUIRED_PARAMETERS_MESSAGE + KEY_STORE_FILE + ", "
                    + KEY_STORE_PASSWORD + ", " + KEY_STORE_KEY_PASSWORD);
        }
        super.validateParameters();
    }
}
