package com.tacitknowledge.simulator.processors;

import java.util.Date;
import java.io.File;

/**
 * Created by IntelliJ IDEA.
 * User: galo
 * Date: Oct 28, 2009
 * Time: 2:24:28 PM
 * To change this template use File | Settings | File Templates.
 */
public class AddTimeStampToMessage {
    public String addTimestampToMessage(String body) {
        return (new Date().toString() + " : " + body + System.getProperty("line.separator"));
    }
}
