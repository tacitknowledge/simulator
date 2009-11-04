package com.tacitknowledge.simulator.transports;

import com.tacitknowledge.simulator.Transport;

/**
 * Transport implementation for File endpoints.
 *
 * @author galo
 */
public class FileTransport extends BaseTransport implements Transport
{
    /**
     * The underlying directory path to poll from/write to.
     */
    private String directoryName;

    /**
     * Name of the file to listen for/write to. Optional
     */
    private String fileName;

    /**
     * File extension of files the transport will only poll from. Optional
     */
    private String fileExtension;

    /**
     * Flag to determine if file should be deleted after processing. For inbound transports only.
     */
    private boolean deleteFile;

    /**
     * @param directoryPath Directory path to poll from/write to
     */
    public FileTransport(String directoryPath)
    {
        this.directoryName = directoryPath;
    }

    /**
     * @inheritDoc
     */
    public String toUriString()
    {
        StringBuilder sb = new StringBuilder("file://");

        // --- directory name
        sb.append(directoryName);

        // --- Options. Take a 2 seconds delay before polling the directory after the route is registered.
        sb.append("?initialDelay=2000");

        // --- fileName & fileExtension should be mutually exclusive options
        if (fileName != null)
        {
            sb.append("&fileName=").append(fileName);
        }
        else if (fileExtension != null)
        {
            // --- File extension is used as a RegEx filter for transport routing
            sb.append("&include=^.*(i)(.").append(fileExtension).append(")");
        }
        if (deleteFile)
        {
            sb.append("&delete=true");
        }

        return sb.toString();
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
     * @return
     * @see #fileExtension
     */
    public String getFileExtension()
    {
        return fileExtension;
    }

    /**
     * @param fileExtension
     * @see #fileExtension
     */
    public void setFileExtension(String fileExtension)
    {
        this.fileExtension = fileExtension;
    }

    /**
     * @return
     * @see #deleteFile
     */
    public boolean getDeleteFile()
    {
        return deleteFile;
    }

    /**
     * @param deleteFile
     * @see #deleteFile
     */
    public void setDeleteFile(boolean deleteFile)
    {
        this.deleteFile = deleteFile;
    }
}
