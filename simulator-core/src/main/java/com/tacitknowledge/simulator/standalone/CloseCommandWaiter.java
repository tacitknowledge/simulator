package com.tacitknowledge.simulator.standalone;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ResourceBundle;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class contains the logic of waiting a close command on a certain port.
 * @author Oleg Ciobanu ociobanu@tacitknowledge.com
 *
 */
public class CloseCommandWaiter extends Thread {
    private static final int BUFFER_LENGTH_10 = 10;
    /**
     * Config file name.
     */
    public static final String CONFIG = "config";
    /**
     * Close port property name.
     */
    public static final String CLOSE_PORT_NAME = "closePort";
    /**
     * Close command property name.
     */
    public static final String CLOSE_COMMAND_NAME = "closeCommand";
    /**
     * Default close command.
     */
    public static final String DEFAULT_CLOSE_COMMAND = "CLOSE";
    /**
     * Default close port.
     */
    public static final int DEFAULT_CLOSE_PORT = 8080;

    /**
     * closePort - port on which application will expect close command.
     */
    private int closePort = DEFAULT_CLOSE_PORT;

    /**
     * closeCommand - is the command that will close the application.
     */
    private String closeCommand = DEFAULT_CLOSE_COMMAND;
    /**
     * Class logger.
     */
    private static Logger logger =
        LoggerFactory.getLogger(CloseCommandWaiter.class);

    /**
     * This variable will be used to communicate
     *  closing procedure to other applicat1ion components.
     */
    private ConversationsScheduledLoader conversationsScheduledLoader = null;

    /**
     * socket on which the application will wait close command.
     */
    private ServerSocket socket = null;

    /**
     * public constructor for this class.
     * @throws IOException is thrown if port binding is failed.
     */
    public CloseCommandWaiter() throws IOException {
        loadConfiguration();
        bindToClosePort();
    }

    /**
     * This method will wait the close command on the listening port.
     */
    private void waitCloseCommand() {
        if (socket != null) {
            try {
                while (true) {
                    Socket incommingConnectionNewSocket = socket.accept();
                    InputStreamReader reader = new InputStreamReader(
                            incommingConnectionNewSocket.getInputStream());
                    StringBuilder readString = new StringBuilder();
                    char[] buffer = new char[BUFFER_LENGTH_10];
                    int read = 0;
                    while ((read = reader.read(buffer)) != -1
                            && readString.length() < closeCommand.length()) {
                        readString.append(buffer, 0, read);
                    }

                    incommingConnectionNewSocket.close();

                    if (readString != null
                            && readString.toString().equals(closeCommand)) {
                        // set toStop member variable for scheduled
                        //conversation loader
                        conversationsScheduledLoader.setToStop(Boolean.TRUE);
                        // close server socket
                        socket.close();
                        break;
                    }
                }
            } catch (IOException e) {
                logger.error(e.getMessage());
            }
        }
    }

    /**
     * Binding to configured close port.
     * @throws IOException error at port binding time
     */
    private void bindToClosePort() throws IOException {
        try {
            socket = new ServerSocket(closePort);
        } catch (IOException e) {
            // could not create listening socket
            logger.error(e.getMessage());
        }
    }

    /**
     * This thread will only wait for close command on a configured port.
     */
    public void run() {
        waitCloseCommand();
    }

    /** close port getter.
     *
     * @return close port
     */
    public int getClosePort() {
        return closePort;
    }

    /** close port setter.
     *
     * @param closePort on which application will listen close command
     */
    public void setClosePort(int closePort) {
        this.closePort = closePort;
    }

    /** stop command getter.
     *
     * @return closeCommand expected close command
     */
    public String getCloseCommand() {
        return closeCommand;
    }

    /** stop command setter.
     *
     * @param stopWord
     */
    public void setCloseCommand(String closeCommand) {
        this.closeCommand = closeCommand;
    }

    /**
     * Read configured close port and close command from config files.
     */
    private void loadConfiguration() {
        ResourceBundle bundle = ResourceBundle.getBundle(CONFIG);
        if (bundle.containsKey(CLOSE_PORT_NAME)) {
            setClosePort(Integer.parseInt(bundle.getString(CLOSE_PORT_NAME)));
        }

        if (bundle.containsKey(CLOSE_COMMAND_NAME)) {
            this.setCloseCommand(bundle.getString(CLOSE_COMMAND_NAME));
        }
    }

    /**toClose getter.
     *
     * @return value of toClose variable
     */
    public ConversationsScheduledLoader getConversationsScheduledLoader() {
        return conversationsScheduledLoader;
    }

    /** FileCmonfigLoader setter.
     *
     * @param conversationsScheduledLoader scheduled conversations loader.
     */
    public void setConversationsScheduledLoader(
            ConversationsScheduledLoader conversationsScheduledLoader) {
        this.conversationsScheduledLoader = conversationsScheduledLoader;
    }
}