package com.tacitknowledge.simulator.processors;

import com.tacitknowledge.simulator.pojos.CsvPojo;

import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: galo
 * Date: Oct 29, 2009
 * Time: 12:36:39 PM
 * To change this template use File | Settings | File Templates.
 */
public class CsvPojoToXml {
    private final static String LB = System.getProperty("line.separator");
    private final static String TAB = "\t";
    private final static String ROW_TAG = "<row>";
    private final static String ROW_END_TAG = "</row>";
    private final static String VALUE_TAG = "<value>";
    private final static String VALUE_END_TAG = "</value>";

    public String csvPojoToXml(CsvPojo body) {
        System.out.println("*** Starting processing of CSV SimulatorPojo with " + body.getRows().size() + " rows");
        StringBuilder sb = new StringBuilder("<csv>" + LB);

        for (List<String> row : body.getRows()) {
            sb.append(generateRowXml(row, TAB));
        }

        sb.append("</csv>");
        return sb.toString();
    }

    private String generateRowXml(List<String> row, String indent) {
        StringBuilder sb = new StringBuilder(indent).append(ROW_TAG).append(LB);

        for (String value : row) {
            generateValueXml(sb, value, indent + TAB);
        }

        sb.append(indent).append(ROW_END_TAG).append(LB);
        return sb.toString();
    }

    private void generateValueXml(StringBuilder sb, String value, String indent) {
        sb.append(indent).append(VALUE_TAG).append(value).append(VALUE_END_TAG).append(LB);
    }
}
