package com.tacitknowledge.simulator.transports;

import com.tacitknowledge.simulator.Transport;

/**
 * Transport implementation for FTP/SFTP endpoints
 *
 * @author Jorge Galindo (jgalindo@tacitknowledge.com)
 */
public class FtpTransport extends BaseTransport implements Transport
{
    /**
     * FTP/SFTP host name
     */
    private String host;

    /**
     * FTP/SFTP port. Optional. Defaults to 21 for FTP and 22 for SFTP
     */
    private String port;

    /**
     * Name of the FTP/SFTP directory to poll from
     */
    private String directoryName;

    /**
     * Username to login as. Optional. If not provided, anonymous login will be attempted
     */
    private String username;

    /**
     * Password used to login to the remote file system. Optional
     */
    private String password;

    /**
     * Flag to determine if this transport is FTP or SFTP. Defaults to FTP
     */
    private boolean sftp;

    /**
     * Flag to determine the file transfer mode, BINARY or ASCII. Defaults to ASCII
     */
    private boolean binary;

    /**
     * Constructor for FtpTransport class
     * @param host @see #host
     * @param directoryName @see #directoryName
     */
    public FtpTransport(String host, String directoryName)
    {
        this.host = host;
        this.directoryName = directoryName;
    }

    /**
     * Constructor for FtpTransport class with sftp option.
     * @param host @see #host
     * @param directoryName @see #directoryName
     * @param sftp @see #sftp
     */
    public FtpTransport(String host, String directoryName, boolean sftp)
    {
        this.host = host;
        this.directoryName = directoryName;
        this.sftp = sftp;
    }

    /**
     * Constructor for FtpTransport class with sftp option and credentials
     * @param host @see #host
     * @param port @see #port
     * @param directoryName @see #directoryName
     * @param username @see #username
     * @param password @see #password
     * @param sftp @see #sftp
     */
    public FtpTransport(String host, String port, String directoryName, String username,
            String password, boolean sftp)
    {
        this.host = host;
        this.port = port;
        this.directoryName = directoryName;
        this.username = username;
        this.password = password;
        this.sftp = sftp;
    }

    /**
     * @inheritDoc
     * @return @see #Transport.toUriString()
     */
    public String toUriString()
    {
        return null;
    }

    /**
     * Getter for @see #host
     * @return @see #host
     */
    public String getHost()
    {
        return host;
    }

    /**
     * Setter for @see #host
     * @param host @see #host
     */
    public void setHost(String host)
    {
        this.host = host;
    }

    /**
     * Getter for @see #port
     * @return @see #port
     */
    public String getPort()
    {
        return port;
    }

    /**
     * Setter for @see #port
     * @param port @see #port
     */
    public void setPort(String port)
    {
        this.port = port;
    }

    /**
     * Getter for @see #directoryName
     * @return @see #directoryName
     */
    public String getDirectoryName()
    {
        return directoryName;
    }

    /**
     * Setter for @see #directoryName
     * @param directoryName @see #directoryName
     */
    public void setDirectoryName(String directoryName)
    {
        this.directoryName = directoryName;
    }

    /**
     * Getter for @see #username
     * @return @see #username
     */
    public String getUsername()
    {
        return username;
    }

    /**
     * Setter for @see #username
     * @param username @see #username
     */
    public void setUsername(String username)
    {
        this.username = username;
    }

    /**
     * Getter for @see #password
     * @return @see #password
     */
    public String getPassword()
    {
        return password;
    }

    /**
     * Setter for @see #password
     * @param password @see #password
     */
    public void setPassword(String password)
    {
        this.password = password;
    }

    /**
     * Getter for @see #sftp
     * @return @see #sftp
     */
    public boolean isSftp()
    {
        return sftp;
    }

    /**
     * Setter for @see #sftp
     * @param sftp @see #sftp
     */
    public void setSftp(boolean sftp)
    {
        this.sftp = sftp;
    }

    /**
     * Getter for @see #binary
     * @return @see #binary
     */
    public boolean isBinary()
    {
        return binary;
    }

    /**
     * Setter for @see #binary
     * @param binary @see #binary
     */
    public void setBinary(boolean binary)
    {
        this.binary = binary;
    }
}
