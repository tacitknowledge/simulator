package com.tacitknowledge.simulator.transports;

/**
 * Transport implementation for FTP/SFTP endpoints
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
     * 
     * @param host
     * @param directoryName
     */
    public FtpTransport(String host, String directoryName)
    {
        this.host = host;
        this.directoryName = directoryName;
    }

    /**
     *
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
     *
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
     * @see #host
     * @return
     */
    public String getHost()
    {
        return host;
    }

    /**
     * @see #host
     * @param host
     */
    public void setHost(String host)
    {
        this.host = host;
    }

    /**
     * @see #port
     * @return
     */
    public String getPort()
    {
        return port;
    }

    /**
     * @see #port
     * @param port
     */
    public void setPort(String port)
    {
        this.port = port;
    }

    /**
     * @see #directoryName
     * @return
     */
    public String getDirectoryName()
    {
        return directoryName;
    }

    /**
     * @see #directoryName
     * @param directoryName
     */
    public void setDirectoryName(String directoryName)
    {
        this.directoryName = directoryName;
    }

    /**
     * @see #username
     * @return
     */
    public String getUsername()
    {
        return username;
    }

    /**
     * @see #username
     * @param username
     */
    public void setUsername(String username)
    {
        this.username = username;
    }

    /**
     * @see #password
     * @return
     */
    public String getPassword()
    {
        return password;
    }

    /**
     * @see #password
     * @param password
     */
    public void setPassword(String password)
    {
        this.password = password;
    }

    /**
     * @see #sftp
     * @return
     */
    public boolean isSftp()
    {
        return sftp;
    }

    /**
     * @see #sftp
     * @param sftp
     */
    public void setSftp(boolean sftp)
    {
        this.sftp = sftp;
    }

    /**
     * @see #binary
     * @return
     */
    public boolean isBinary()
    {
        return binary;
    }

    /**
     * @see #binary
     * @param binary
     */
    public void setBinary(boolean binary)
    {
        this.binary = binary;
    }
}
