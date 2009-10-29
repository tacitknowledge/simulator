package com.tacitknowledge.simulator.test;

import junit.framework.TestCase;
import org.apache.camel.CamelContext;
import org.apache.camel.spring.SpringCamelContext;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import com.tacitknowledge.simulator.common.SimulatorConstants;

import java.io.*;

/**
 * Created by IntelliJ IDEA.
 * User: galo
 * Date: Oct 29, 2009
 * Time: 10:39:57 AM
 * To change this template use File | Settings | File Templates.
 */
public class SimulatorTestBase extends TestCase {
    protected final static String RESOURCES_FOLDER = "src/main/resources/";
    protected final static String SOURCE_FOLDER = "original_files/";
    protected final static String INBOX_FOLDER = "inbox/";
    protected final static String OUTBOX_FOLDER = "outbox";    

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

    protected CamelContext getContext() {
        return context;
    }


    protected void copyFile(File origFile, File copiedFile) {
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
