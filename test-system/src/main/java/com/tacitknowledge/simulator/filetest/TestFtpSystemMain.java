package com.tacitknowledge.simulator.filetest;

import org.apache.ftpserver.FtpServer;
import org.apache.ftpserver.FtpServerFactory;
import org.apache.ftpserver.ftplet.FtpException;
import org.apache.ftpserver.listener.ListenerFactory;
import org.apache.ftpserver.usermanager.Md5PasswordEncryptor;
import org.apache.ftpserver.usermanager.impl.PropertiesUserManager;

import java.io.File;

/**
 * Date: 03.12.2009
 * Time: 12:29:02
 *
 * @author Nikita Belenkiy (nbelenkiy@tacitknowledge.com)
 */
public class TestFtpSystemMain {
    public static void main(String[] args) throws FtpException {
        FtpServerFactory serverFactory = new FtpServerFactory();
        serverFactory.setUserManager(new PropertiesUserManager(new Md5PasswordEncryptor(),new File("user.properties"),"admin"));
        ListenerFactory factory = new ListenerFactory();
        factory.setPort(2221);
        serverFactory.addListener("default", factory.createListener());
        FtpServer server = serverFactory.createServer();

        // start the server
        server.start();
    }
}
