package com.tacitknowledge.simulator.filetest;

import java.io.*;

/**
 * This class represents a system that will wait for a file to be copied to a destination folder
 * this class is needed to test that the simulator is working
 *
 * @author Oscar Gonzalez (oscar@tacitknowledge.com)
 */
public class TestFileSystemMain
{
    /**
     * Name of the source file to be read
     */
    public static final String SOURCE_FILE = "xxx.xml";

    /**
     * Source directory where the file to be read lives
     */
    public static final String SOURCE_DIR = "/Users/nikitabelenkiy/Simulator2/trunk/simulator/web-ui/src/main/rubyapp/12345/";

    /** Target file for the results */
    public static final String TARGET_FILE = "zzz.xml";


    /** target directory to copy the files to */
    public static final String TARGET_DIR = "/Users/nikitabelenkiy/Simulator2/trunk/simulator/web-ui/src/main/rubyapp/123456";

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
                = TestFileSystemMain.class.getClassLoader().getResourceAsStream(SOURCE_FILE);
        String s = SOURCE_DIR + File.separator + SOURCE_FILE;
        File file = new File(s);
        FileOutputStream fos
                = new FileOutputStream(s);
        System.out.println("creating file "+file.getAbsolutePath());

        byte[] buffer = new byte[BUFFER];
        int bytesRead = 0;

        while ((bytesRead = inputStream.read(buffer)) > 0)
        {
            fos.write(buffer, 0, bytesRead);
        }
        fos.close();
        inputStream.close();

        File targetFile = new File(TARGET_DIR + File.separator + TARGET_FILE);
        System.out.println("Created");
        System.out.println("Waiting for result file "+targetFile.getAbsolutePath());

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
        System.out.println("Got the result file");

        // TODO add more assertions here
        //assert (strContent.toString().contains("simulator was here"));
    }
}
