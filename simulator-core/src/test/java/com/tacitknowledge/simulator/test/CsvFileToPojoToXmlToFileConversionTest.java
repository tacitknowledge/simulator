package com.tacitknowledge.simulator.test;

import com.tacitknowledge.simulator.common.SimulatorConstants;
import com.tacitknowledge.simulator.DynamicRouteBuilder;

import java.util.List;
import java.util.ArrayList;
import java.io.File;

/**
 * Created by IntelliJ IDEA.
 * User: galo
 * Date: Oct 29, 2009
 * Time: 10:45:52 AM
 * To change this template use File | Settings | File Templates.
 */
public class CsvFileToPojoToXmlToFileConversionTest extends SimulatorTestBase {
    private final static String CSV_FILE_NAME = "csv-test";

    public void testCsvFileToPojoToLogFile() {
        String outFilePath = RESOURCES_FOLDER + OUTBOX_FOLDER;
        String outFileName = CSV_FILE_NAME + SimulatorConstants.EXT_XML;

        // --- Create and register route dinamycally
        // --- from endpoint. Let's use a file-paste into our inbox folder as entry, triggering the route with .csv files only
        String from = "file://" + RESOURCES_FOLDER + INBOX_FOLDER + "?delete=true&include=.*(?i)(.csv)$";
        // --- Now, the to endpoints
        List<String> tos = new ArrayList<String>();
        // --- First to: our processor from Csv file to Csv pojo
        tos.add("bean:csvFileToPojo");
        // --- Second: processor to convert from Csv pojo to an Xml String...
        tos.add("bean:csvPojoToXml");
        // --- Third and final to: send the string result to a camel-generated file in our outbox folder
        tos.add("file://" + outFilePath + "?fileName=" + outFileName);

        DynamicRouteBuilder builder = new DynamicRouteBuilder(getContext());
        builder.createNewCamelRoute(from, tos);

         // --- Now, let's trigger the event
        File file1 = new File(RESOURCES_FOLDER + SOURCE_FOLDER + CSV_FILE_NAME + SimulatorConstants.EXT_CSV);
        File file2 = new File(RESOURCES_FOLDER + INBOX_FOLDER + CSV_FILE_NAME + SimulatorConstants.EXT_CSV);
        copyFile(file1, file2);

        try {
            // Sleep for a couple seconds
            Thread.sleep(2 * 1000);

            File logFile = new File(outFilePath + "/" + outFileName);
            assertTrue("Log file should exist!", logFile.exists());
            assertTrue("Log file should have something at least!", logFile.length() > 0);
        } catch(Exception e) {
            e.printStackTrace();
        }
    }
}