package com.tacitknowledge.simulator.test;

//import java.util.List;
//import java.util.ArrayList;
//
//import com.tacitknowledge.simulator.DynamicRouteBuilder;
//import com.tacitknowledge.simulator.test.SimulatorConstants;


//import java.io.*;

/**
 *
 *
 * @author galo
 */
public class DynamicRouteBuilderTest_ /*extends SimulatorTestBaseTest_*/ {
//    private final static String PLAIN_TEXT_FILE_NAME = "plain-text-test";
//    private final static String LOG_FILE_NAME = "log";
//
//    public void testNewSimpleRoute() {
//        // --- First, create new route on the fly
//        DynamicRouteBuilder builder = new DynamicRouteBuilder(getContext());
//
//        String logFilePath = RESOURCES_FOLDER + OUTBOX_FOLDER;
//        String logFileName = LOG_FILE_NAME + SimulatorConstants.EXT_PLAIN_TEXT;
//
//        // --- STEP BY STEP
//        // --- from endpoint. Let's use a file-paste into our inbox folder as entry
//        String from = "file://" + RESOURCES_FOLDER + INBOX_FOLDER + "?delete=true";
//        // --- Now, the to endpoints
//        List<String> tos = new ArrayList<String>();
//        // --- First to: our processor
//        tos.add("bean:addTimestampToMessage");
//        // --- Second and final to: send the result to a log file in our outbox folder
//        tos.add("file://" + logFilePath + "?fileName=" + logFileName + "&fileExist=Append");
//        //tos.add("file://src/main/resources/outbox?fileName=incidentsLog.txt&fileExist=Append");
//
//        builder.createNewCamelRoute(from, tos);
//
//        // --- Now, let's trigger the event
//        File file1 = new File(RESOURCES_FOLDER + SOURCE_FOLDER + PLAIN_TEXT_FILE_NAME + SimulatorConstants.EXT_PLAIN_TEXT);
//        File file2 = new File(RESOURCES_FOLDER + INBOX_FOLDER + PLAIN_TEXT_FILE_NAME + SimulatorConstants.EXT_PLAIN_TEXT);
//
//        //System.out.println("logFile: " + logFile.getAbsolutePath());
//
//        copyFile(file1, file2);
//        try {
//            // Sleep for a couple seconds
//            Thread.sleep(2 * 1000);
//
//            // --- Let's start clean with the log file. If it exists, delete it.
//            /*if (logFile.exists()) {
//                logFile.delete();
//            }
//            assertTrue("Log file should not exist", !logFile.exists());*/
//
//            // --- Sleep again, let's give camel time to catch the copied file and process it
//            Thread.sleep(2 * 1000);
//
//            // --- Copy again the same file but with another name
//            File file3 =
//                    new File(RESOURCES_FOLDER + SOURCE_FOLDER + PLAIN_TEXT_FILE_NAME + "2" + SimulatorConstants.EXT_PLAIN_TEXT);
//            File file4 =
//                    new File(RESOURCES_FOLDER + INBOX_FOLDER + PLAIN_TEXT_FILE_NAME + "2" + SimulatorConstants.EXT_PLAIN_TEXT);
//            copyFile(file3, file4);
//
//            // --- Sleep again, let's give camel time to catch the AGAIN copied file and process it
//            Thread.sleep(2 * 1000);
//
//            File logFile = new File(logFilePath + "/" + logFileName);
//            assertTrue("Log file should exist!", logFile.exists());
//            assertTrue("Log file should have something at least!", logFile.length() > 0);
//
//            System.out.println("Finished");
//        } catch(Exception e) {
//            e.printStackTrace();
//        }
//    }
}