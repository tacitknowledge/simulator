package com.tacitknowledge.simulator.test;

import java.util.Date;

/**
 *
 *
 * @author galo
 */
public class AddTimeStampToMessage {
    public String addTimestampToMessage(String body) {
        return (new Date().toString() + " : " + body + System.getProperty("line.separator"));
    }
}
