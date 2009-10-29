package com.tacitknowledge.simulator.pojos;

import java.util.List;
import java.util.ArrayList;

/**
 * Simple wrapper around a CSV file, emulates rows and columns from the CSV file.
 * Number of columns in rows are not guaranteed to be all the same. 
 *
 * Created by IntelliJ IDEA.
 * User: galo
 * Date: Oct 29, 2009
 * Time: 9:19:34 AM
 */
public class CsvPojo {
    private List<List<String>> rows;

    public CsvPojo() {
        rows = new ArrayList<List<String>>();
    }

    public List<List<String>> getRows() {
        return rows;
    }

    public void setRows(List<List<String>> rows) {
        this.rows = rows;
    }

    public List getRow(int row) {
        return rows.get(row);
    }

    public void addRow(List<String> row) {
        rows.add(row);
    }

    /**
     * Returns the String value in a given "cell" or null if the arguments are off limits.
     * @param row Row number, starting from 0
     * @param col Column number, starting from 0
     * @return
     */
    public String getValue(int row, int col) {
        if (rows.size() > row) {
            if (rows.get(row).size() > col) {
                return rows.get(row).get(col);
            }
        }
        return null;
    }
}