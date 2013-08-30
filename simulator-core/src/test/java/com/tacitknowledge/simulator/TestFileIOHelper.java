package com.tacitknowledge.simulator;

import java.io.*;

/**
 * Created by IntelliJ IDEA.
 * User: mshort
 * Date: 8/30/13
 * Time: 9:15 AM
 * To change this template use File | Settings | File Templates.
 */
public class TestFileIOHelper {
    public static final String RESOURCES_PATH = "simulator-core/src/test/resources/";
    public static final String ORIGINAL_FILES_PATH = RESOURCES_PATH + "original_files/";
    public static final String GIVEX_FILES_PATH = RESOURCES_PATH + "givex/";

    /**
     *
     * @param filePathName The path and name of the file to be read
     * @return The file contents as a String
     * @throws Exception If anything goes wrong
     */
    public static String readFile(String filePathName)
            throws Exception
    {
        return readFile(new File(filePathName));
    }

    /**
     *
     * @param file The File to be read
     * @return The file contents as a String
     * @throws Exception If anything goes wrong
     */
    public static String readFile(File file)
            throws Exception
    {
        // --- Make sure file1 exists
        if (!file.exists())
        {
            throw new Exception("Original file must exist: " + file.getAbsolutePath());
        }

        InputStream is = new FileInputStream(file);
        StringBuilder sb = new StringBuilder();
        String line;

        try
        {
            BufferedReader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));

            while ((line = reader.readLine()) != null)
            {
                sb.append(line).append("\n");
            }

            return sb.toString();
        }
        finally
        {
            is.close();
        }
    }

    public static void copyFile(File file1, File file2)
        throws Exception
    {
        // --- Make sure file1 exists
        if (!file1.exists())
        {
            throw new Exception("Original file must exist: " + file1.getAbsolutePath());
        }

        InputStream in = new FileInputStream(file1);
        OutputStream out = new FileOutputStream(file2);

        byte[] buf = new byte[1024];
        int len;
        while ((len = in.read(buf)) > 0)
        {
            out.write(buf, 0, len);
        }
        in.close();
        out.close();
        System.out.println("File copied to " + file2.getAbsolutePath());
    }
}
