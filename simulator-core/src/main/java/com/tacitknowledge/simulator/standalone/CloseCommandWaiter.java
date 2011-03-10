package com.tacitknowledge.simulator.standalone;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tacitknowledge.simulator.utils.Configuration;

/**
 * This class contains the logic of waiting a close command on a certain port.
 * @author Oleg Ciobanu ociobanu@tacitknowledge.com
 *
 */
public class CloseCommandWaiter extends Thread
{
    /**
     * Length of the reading buffer. 
     */
    private static final int BUFFER_LENGTH_10 = 10;

    /**
     * Class logger.
     */
    private static Logger logger = LoggerFactory.getLogger(CloseCommandWaiter.class);

    /**
     * This variable will be used to communicate
     *  closing procedure to other applicat1ion components.
     */
    private ScheduledConversationsLoader conversationsScheduledLoader = null;

    /**
     * socket on which the application will wait close command.
     */
    private ServerSocket socket = null;

    /**
     * public constructor for this class.
     * @throws IOException is thrown if port binding is failed.
     */
    public CloseCommandWaiter() throws IOException
    {
        bindToClosePort();
    }

    /**
     * This method will wait the close command on the listening port.
     */
    private void waitCloseCommand()
    {
        if (socket != null)
        {
            try
            {
                while (true)
                {
                    Socket incommingConnectionNewSocket = socket.accept();
                    InputStreamReader reader = new InputStreamReader(
                            incommingConnectionNewSocket.getInputStream());
                    StringBuilder readString = new StringBuilder();
                    char[] buffer = new char[BUFFER_LENGTH_10];
                    int read = 0;
                    while ((read = reader.read(buffer)) != -1
                            && readString.length() < Configuration.getPropertyAsString(
                                    Configuration.CLOSE_COMMAND_NAME).length())
                    {
                        readString.append(buffer, 0, read);
                    }

                    incommingConnectionNewSocket.close();

                    if (readString != null
                            && readString.toString().equals(
                                    Configuration
                                            .getPropertyAsString(Configuration.CLOSE_COMMAND_NAME)))
                    {
                        // set toStop member variable for scheduled
                        //conversation loader
                        conversationsScheduledLoader.setToStop(Boolean.TRUE);
                        // close server socket
                        socket.close();
                        break;
                    }
                }
            }
            catch (IOException e)
            {
                logger.error(e.getMessage());
            }
        }
    }

    /**
     * Binding to configured close port.
     * @throws IOException error at port binding time
     */
    private void bindToClosePort() throws IOException
    {
        try
        {
            socket = new ServerSocket(Configuration.getPropertyAsInt(Configuration.CLOSE_PORT_NAME));
        }
        catch (IOException e)
        {
            // could not create listening socket
            logger.error(e.getMessage());
        }
    }

    /**
     * This thread will only wait for close command on a configured port.
     */
    public void run()
    {
        waitCloseCommand();
    }

    /**
     * Conversation scheduled loader getter.
     * @return value of toClose variable
     */
    public ScheduledConversationsLoader getConversationsScheduledLoader()
    {
        return conversationsScheduledLoader;
    }

    /**
     * Conversation scheduled loader  setter.
     * @param conversationsScheduledLoader scheduled conversations loader.
     */
    public void setConversationsScheduledLoader(
            ScheduledConversationsLoader conversationsScheduledLoader)
    {
        this.conversationsScheduledLoader = conversationsScheduledLoader;
    }
}
