package com.tacitknowledge.simulator.standalone;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.InetSocketAddress;
import java.net.Socket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tacitknowledge.simulator.utils.Configuration;

/**
 * This class is a stops application running in standalone mode.
 * @author Oleg Ciobanu ociobanu@tacitknowledge.com
 *
 */
public class StandAloneStopper
{
    /**
     * class logger.
     */
    private static Logger logger = LoggerFactory.getLogger(StandAloneStopper.class);

    /**
     * constructor.
     */
    public StandAloneStopper()
    {}

    /**
     * send close command on the close port.
     */
    public void sendCloseCommand()
    {
        Socket socket = new Socket();
        try
        {
            socket.connect(new InetSocketAddress(Configuration
                    .getPropertyAsString(Configuration.HOST_NAME), Configuration
                    .getPropertyAsInt(Configuration.CLOSE_PORT_NAME)));
            OutputStreamWriter writer = new OutputStreamWriter(socket.getOutputStream());
            writer.write(Configuration.getPropertyAsString(Configuration.CLOSE_COMMAND_NAME));
            writer.flush();
            writer.close();
        }
        catch (IOException e)
        {
            logger.error(e.getMessage());
        }
        finally
        {
            if (socket.isConnected())
            {
                try
                {
                    socket.close();
                }
                catch (IOException exception)
                {
                    logger.error(exception.getMessage());
                }
            }
        }
    }

    /**
     * entry point for the program that will stop the
     *  simulator running in standalone mode.
     * @param args
     */
    public static void main(String[] args)
    {
        StandAloneStopper stopper = new StandAloneStopper();
        stopper.sendCloseCommand();
    }
}
