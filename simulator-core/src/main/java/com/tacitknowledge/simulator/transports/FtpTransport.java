package com.tacitknowledge.simulator.transports;

import com.tacitknowledge.simulator.Transport;

/**
 * Transport implementation for FTP/SFTP endpoints
 *
 * @author galo
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
     * @param host
     * @param directoryName
     */
    public FtpTransport(String host, String directoryName)
    {
        this.host = host;
        this.directoryName = directoryName;
    }

    /**
     * @param host
     * @param directoryName
     * @param sftp
     */
    public FtpTransport(String host, String directoryName, boolean sftp)
    {
        this.host = host;
        this.directoryName = directoryName;
        this.sftp = sftp;
    }

    /**
     * @param host
     * @param port
     * @param directoryName
     * @param username
     * @param password
     * @param sftp
     */
    public FtpTransport(String host, String port, String directoryName, String username, String password, boolean sftp)
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
     */
    public String toUriString()
    {
        return null;
    }

    /**
     * @return
     * @see #host
     */
    public String getHost()
    {
        return host;
    }

    /**
     * @param host
     * @see #host
     */
    public void setHost(String host)
    {
        this.host = host;
    }

    /**
     * @return
     * @see #port
     */
    public String getPort()
    {
        return port;
    }

    /**
     * @param port
     * @see #port
     */
    public void setPort(String port)
    {
        this.port = port;
    }

    /**
     * @return
     * @see #directoryName
     */
    public String getDirectoryName()
    {
        return directoryName;
    }

    /**
     * @param directoryName
     * @see #directoryName
     */
    public void setDirectoryName(String directoryName)
    {
        this.directoryName = directoryName;
    }

    /**
     * @return
     * @see #username
     */
    public String getUsername()
    {
        return username;
    }

    /**
     * @param username
     * @see #username
     */
    public void setUsername(String username)
    {
        this.username = username;
    }

    /**
     * @return
     * @see #password
     */
    public String getPassword()
    {
        return password;
    }

    /**
     * @param password
     * @see #password
     */
    public void setPassword(String password)
    {
        this.password = password;
    }

    /**
     * @return
     * @see #sftp
     */
    public boolean isSftp()
    {
        return sftp;
    }

    /**
     * @param sftp
     * @see #sftp
     */
    public void setSftp(boolean sftp)
    {
        this.sftp = sftp;
    }

    /**
     * @return
     * @see #binary
     */
    public boolean isBinary()
    {
        return binary;
    }

    /**
     * @param binary
     * @see #binary
     */
    public void setBinary(boolean binary)
    {
        this.binary = binary;
    }
}
