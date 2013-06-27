package com.tacitknowledge.simulator.filetest;

import org.apache.ftpserver.FtpServer;
import org.apache.ftpserver.FtpServerFactory;
import org.apache.ftpserver.ftplet.FtpException;
import org.apache.ftpserver.listener.ListenerFactory;
import org.apache.ftpserver.usermanager.Md5PasswordEncryptor;
import org.apache.ftpserver.usermanager.impl.PropertiesUserManager;

import java.io.File;

/**
 * This class creates an FTP system and starts it
 *
 * @author Nikita Belenkiy (nbelenkiy@tacitknowledge.com)
 */
public final class TestFtpSystemMain
{

    /**
     * Default private constructor
     */
    private TestFtpSystemMain()
    {

    }

    /**
     * Emulates an FTP server
     * @param args - provided arguments
     * @throws FtpException - if an error occurs
     */
    public static void main(final String[] args) throws FtpException
    {
        FtpServerFactory serverFactory = new FtpServerFactory();
        serverFactory.setUserManager(new PropertiesUserManager(new Md5PasswordEncryptor(),
                new File("user.properties"), "admin"));
        ListenerFactory factory = new ListenerFactory();
        factory.setPort(2221);
        serverFactory.addListener("default", factory.createListener());
        FtpServer server = serverFactory.createServer();

        // start the server
        server.start();
    }

}
