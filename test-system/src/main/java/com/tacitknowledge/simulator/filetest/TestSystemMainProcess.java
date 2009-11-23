package com.tacitknowledge.simulator.filetest;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * This class represents a system that will wait for a file to be copied to a destination folder
 * this class is needed to test that the simulator is working
 *
 * @author Oscar Gonzalez (oscar@tacitknowledge.com)
 */
public class TestSystemMainProcess
{
    /**
     * Name of the source file to be read
     */
    public static final String SOURCE_FILE = "test.properties";

    /**
     * Source directory where the file to be read lives
     */
    public static final String SOURCE_DIR = "source";

    /** Target file for the results */
    public static final String TARGET_FILE = "target.properties";


    /** target directory to copy the files to */
    public static final String TARGET_DIR = "target";

    /** Size of the buffer to read */
    public static final int BUFFER = 1024;

    /** Time to wait between file checks     */
    public static final int WAITING_TIME_FOR_FILE = 2000;

    /**
     * This method will test that simulator succesfully copied a file to the destination folder
     *
     * @param args the args to run this program with
     * @throws IOException          if we couldn't read the files
     * @throws InterruptedException if there was a concurrency problem
     */
    public static void main(String[] args) throws IOException, InterruptedException
    {

        InputStream inputStream
                = TestSystemMainProcess.class.getClassLoader().getResourceAsStream(SOURCE_FILE);
        FileOutputStream fos
                = new FileOutputStream(SOURCE_DIR + File.separator + SOURCE_FILE);


        byte[] buffer = new byte[BUFFER];
        int bytesRead = 0;

        while ((bytesRead = inputStream.read(buffer)) > 0)
        {
            fos.write(buffer, 0, bytesRead);
        }
        fos.close();
        inputStream.close();

        File targetFile = new File(TARGET_DIR + File.separator + TARGET_FILE);

        // Wait until the simulator copies the file to the destination forlder
        while (!targetFile.exists())
        {
            Thread.sleep(WAITING_TIME_FOR_FILE);
        }

        StringBuffer strContent = new StringBuffer("");
        FileInputStream fis = new FileInputStream(targetFile);

        int ch;
        while ((ch = fis.read()) != -1)
        {
            strContent.append((char) ch);
        }
        fis.close();

        // TODO add more assertions here
        //assert (strContent.toString().contains("simulator was here"));
    }
}
