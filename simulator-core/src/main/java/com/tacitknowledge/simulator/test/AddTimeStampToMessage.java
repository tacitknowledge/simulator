package com.tacitknowledge.simulator.test;

import java.util.Date;

/**
 * @author Jorge Galindo (jgalindo@tacitknowledge.com)
 */
public class AddTimeStampToMessage
{
    public String addTimestampToMessage(String body)
    {
        return (new Date().toString() + " : " + body + System.getProperty("line.separator"));
    }
}
