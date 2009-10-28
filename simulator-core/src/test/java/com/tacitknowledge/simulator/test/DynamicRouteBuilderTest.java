package com.tacitknowledge.simulator.test;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.apache.camel.spring.SpringCamelContext;

import java.util.List;
import java.util.ArrayList;

import org.apache.camel.ContextTestSupport;
import org.apache.camel.CamelContext;

import com.tacitknowledge.simulator.DynamicRouteBuilder;
import com.tacitknowledge.simulator.common.SimulatorConstants;


import javax.jms.*;
import java.util.ArrayList;
import java.util.List;
import java.io.*;

import junit.framework.TestCase;

/**
 * Created by IntelliJ IDEA.
 * User: galo
 * Date: Oct 28, 2009
 * Time: 10:21:42 AM
 * To change this template use File | Settings | File Templates.
 */
public class DynamicRouteBuilderTest extends TestCase {
    private final static String RESOURCES_FOLDER = "simulator-core/src/main/resources/";
    private final static String SOURCE_FOLDER = "original_files/";
    private final static String INBOX_FOLDER = "inbox/";
    private final static String OUTBOX_FOLDER = "outbox";

    private final static String PLAIN_TEXT_FILE_NAME = "plain-text-test";
    private final static String LOG_FILE_NAME = "log";

    private CamelContext context;

    @Override
    protected void setUp() throws Exception {
        ApplicationContext appContext = new ClassPathXmlApplicationContext(SimulatorConstants.SPRING_CAMEL_CONFIG_FILE);
        context = new SpringCamelContext(appContext);
        context.start();
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
        context.stop();
    }

    public void testNewSimpleRoute() {
        // --- First, create new route on the fly
        DynamicRouteBuilder builder = new DynamicRouteBuilder(context);

        String logFilePath = RESOURCES_FOLDER + OUTBOX_FOLDER;
        String logFileName = LOG_FILE_NAME + SimulatorConstants.EXT_PLAIN_TEXT;

        // --- STEP BY STEP
        // --- from endpoint. Let's use a file-paste into our inbox folder as entry
        String from = "file://" + RESOURCES_FOLDER + INBOX_FOLDER + "?delete=true";
        // --- Now, the to endpoints
        List<String> tos = new ArrayList<String>();
        // --- First to: our processor
        tos.add("bean:addTimestampToMessage");
        // --- Second and final to: send the result to a log file in our outbox folder
        tos.add("file://" + logFilePath + "?fileName=" + logFileName + "&fileExist=Append");
        //tos.add("file://src/main/resources/outbox?fileName=incidentsLog.txt&fileExist=Append");

        builder.createNewCamelRoute(from, tos);

        // --- Now, let's trigger the event
        File file1 = new File(RESOURCES_FOLDER + SOURCE_FOLDER + PLAIN_TEXT_FILE_NAME + SimulatorConstants.EXT_PLAIN_TEXT);
        File file2 = new File(RESOURCES_FOLDER + INBOX_FOLDER + PLAIN_TEXT_FILE_NAME + SimulatorConstants.EXT_PLAIN_TEXT);

        //System.out.println("logFile: " + logFile.getAbsolutePath());

        copyFile(file1, file2);
        try {
            // Sleep for a couple seconds
            Thread.sleep(2 * 1000);

            // --- Let's start clean with the log file. If it exists, delete it.
            /*if (logFile.exists()) {
                logFile.delete();
            }
            assertTrue("Log file should not exist", !logFile.exists());*/

            // --- Sleep again, let's give camel time to catch the copied file and process it
            Thread.sleep(2 * 1000);

            // --- Copy again the same file but with another name
            File file3 =
                    new File(RESOURCES_FOLDER + SOURCE_FOLDER + PLAIN_TEXT_FILE_NAME + "2" + SimulatorConstants.EXT_PLAIN_TEXT);
            File file4 =
                    new File(RESOURCES_FOLDER + INBOX_FOLDER + PLAIN_TEXT_FILE_NAME + "2" + SimulatorConstants.EXT_PLAIN_TEXT);
            copyFile(file3, file4);

            // --- Sleep again, let's give camel time to catch the AGAIN copied file and process it
            Thread.sleep(2 * 1000);

            File logFile = new File(logFilePath + "/" + logFileName);
            assertTrue("Log file should exist!", logFile.exists());
            assertTrue("Log file should have something at least!", logFile.length() > 0);
            
            System.out.println("Finished");
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    private void copyFile(File origFile, File copiedFile) {
        try {
            // Sleep for a couple seconds
            Thread.sleep(2 * 2000);

            // --- Make sure file1 exists
            if (!origFile.exists()) {
                throw new Exception("Original file must exist: " + origFile.getAbsolutePath());
            }

            InputStream in = new FileInputStream(origFile);
            OutputStream out = new FileOutputStream(copiedFile);

            byte[] buf = new byte[1024];
            int len;
            while ((len = in.read(buf)) > 0) {
                out.write(buf, 0, len);
            }
            in.close();
            out.close();
            System.out.println("File copied into " + copiedFile.getAbsolutePath());
        } catch(Exception e) {
            e.printStackTrace();
        }
    }
}