package com.tacitknowledge.simulator.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tacitknowledge.simulator.standalone.Simulator;

/**
 * Configurations reading logic, constants definitions.
 * @author Oleg Ciobanu (ociobanu@tacitknowledge.com)
 * 
 */
public class Configuration
{
    /**
     * Default close command.
     */
    public static final String DEFAULT_CLOSE_COMMAND = "CLOSE";

    /**
     * Default close port.
     */
    public static final String DEFAULT_CLOSE_PORT = "8080";

    /**
     * Close port property name.
     */
    public static final String CLOSE_PORT_NAME = "closePort";

    /**
     * Close command property name.
     */
    public static final String CLOSE_COMMAND_NAME = "closeCommand";

    /**
     * Host property name.
     */
    public final static String HOST_NAME = "host";

    /**
     * Default host name.
     */
    public final static String DEFAULT_HOST_NAME = "localhost";

    /**
     * systems directory property name
     */
    public static final String SYSTEMS_DIRECTORY_NAME = "systemsDirectory";

    /**
     * default systems directory property name
     */
    public static final String DEFAULT_SYSTEMS_DIRECTORY = "systems";

    /**
     * Default conversation reading frequency.
     */
    public static final String DEFAULT_CONVERSATIONS_READING_FREQUENCY = "10000";

    /**
     * Property name for reading frequency.
     */
    public static final String CONVERSATIONS_READING_FREQUENCY_NAME = "conversationsReadingFrequency";

    public static String path = "";

    /**
     * class logger.
     */
    private static Logger logger = LoggerFactory.getLogger(Simulator.class);



    /**
     * Fetch property value as String.
     *
     * @param propertyName - configured property name
     * @return property value as string
     */
    public static String getPropertyAsString(String propertyName)
    {
        String value = System.getProperty(propertyName);

        if (value == null)
        {
            value = getDefaultValue(propertyName);
            logger.warn("Property '" + propertyName + "' was not provider as a system parameter,"
                + " using default value '" + value + "'");
        }

        return value;
    }

    /**
     * Fetch property value as int.
     * @param propertyName - configured property name
     * @return property value as int or -1 
     */
    public static int getPropertyAsInt(String propertyName)
    {
        String propertyValue = getPropertyAsString(propertyName);
        if (propertyValue != null)
        {
            try
            {
                return Integer.parseInt(propertyValue);
            }
            catch (NumberFormatException e)
            {
                logger.error(e.getMessage());
            }
        }
        return -1;
    }

    public static String getDefaultValue(String propertyName)
    {
        if (propertyName.equals(CLOSE_COMMAND_NAME))
        {
            return DEFAULT_CLOSE_COMMAND;
        }
        else if (propertyName.equals(CLOSE_PORT_NAME))
        {
            return DEFAULT_CLOSE_PORT;
        }
        else if (propertyName.equals(HOST_NAME))
        {
            return DEFAULT_HOST_NAME;
        }
        else if (propertyName.equals(CONVERSATIONS_READING_FREQUENCY_NAME))
        {
            return DEFAULT_CONVERSATIONS_READING_FREQUENCY;
        }
        else if (propertyName.equals(SYSTEMS_DIRECTORY_NAME))
        {
            return DEFAULT_SYSTEMS_DIRECTORY;
        }
        return null;
    }
}
