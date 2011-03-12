package com.tacitknowledge.simulator.utils;

import java.io.File;

public final class ConversationUtil
{
    /**
     * Default constructor
     */
    private ConversationUtil()
    {}

    /**
     * last modified date of given file
     * 
     * @param directory parent directory
     * @param fileName file name (without path)
     * @return last modified date. If file doesn't exist or IO error has occurred the method will return 0
     */
    public static long getFileModifiedDate(String directory, String fileName)
    {
        File file = new File(directory, fileName);
        return file.lastModified();
    }

    /**
     * last modified date of given file
     * 
     * @param fileName file name (absolute or relative path)
     * @return last modified date. If file doesn't exist or IO error has occurred the method will return 0
     */
    public static long getFileModifiedDate(String fileName)
    {
        File file = new File(fileName);
        return file.lastModified();
    }
}
