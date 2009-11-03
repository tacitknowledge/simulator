package com.tacitknowledge.simulator.test;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

/**
 * @author Jorge Galindo (jgalindo@tacitknowledge.com)
 */
public class CsvFileToPojo
{
    public CsvPojo csvFileToPojo(File body)
    {
        System.out.println("*** Starting processing of CSV file " + body.getAbsolutePath());
        return populatePojo(body);
    }

    private CsvPojo populatePojo(File body)
    {
        CsvPojo pojo = new CsvPojo();
        try
        {
            // Get the object of DataInputStream
            DataInputStream in = new DataInputStream(new FileInputStream(body));
            BufferedReader br = new BufferedReader(new InputStreamReader(in));

            String strLine;
            while ((strLine = br.readLine()) != null)
            {
                pojo.addRow(populateLine(strLine));
            }

            in.close();
        }
        catch (Exception e)
        {
            System.err.println("*** Error while trying to populate CsvPojo from file " + body.getAbsolutePath());
            e.printStackTrace();
        }
        return pojo;
    }

    private List<String> populateLine(String line)
    {
        StringTokenizer st = new StringTokenizer(line, ",");
        List<String> row = new ArrayList<String>();
        if (st.hasMoreTokens())
        {
            while (st.hasMoreTokens())
            {
                row.add(st.nextToken());
            }
        }
        return row;
    }
}
