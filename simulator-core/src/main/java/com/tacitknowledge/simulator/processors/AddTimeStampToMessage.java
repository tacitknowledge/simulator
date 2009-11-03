package com.tacitknowledge.simulator.processors;

import java.util.Date;
import java.io.File;

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
