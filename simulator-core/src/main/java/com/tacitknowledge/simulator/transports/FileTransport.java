package com.tacitknowledge.simulator.transports;

import com.tacitknowledge.simulator.transports.Transport;

/**
 * Transport implementation for File endpoints.
 * @author galo
 */
public class FileTransport extends BaseTransport implements Transport
{
    /**
     * The directory path to poll from. Can be relative or absolute
     */
    private String directoryPath;

    /**
     * Name of the file to listen for (optional)
     */
    private String fileName;

    /**
     * @param directoryPath
     * @param fileName
     */
    public FileTransport(String directoryPath, String fileName)
    {
        this.directoryPath = directoryPath;
        this.fileName = fileName;
    }

    /**
     * @return
     * @see #directoryPath
     */
    public String getDirectoryPath()
    {
        return directoryPath;
    }

    /**
     * @param directoryPath
     * @see #directoryPath
     */
    public void setDirectoryPath(String directoryPath)
    {
        this.directoryPath = directoryPath;
    }

    /**
     * @return
     * @see #fileName
     */
    public String getFileName()
    {
        return fileName;
    }

    /**
     * @param fileName
     * @see #fileName
     */
    public void setFileName(String fileName)
    {
        this.fileName = fileName;
    }

    /**
     * @inheritDoc
     */
    public String toUriString()
    {
        return null;
    }
}
