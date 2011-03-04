package com.tacitknowledge.simulator;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ResourceBundle;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tacitknowledge.simulator.camel.RouteManagerImpl;
import com.tacitknowledge.simulator.impl.ConversationManagerImpl;

/**
 * Entry point for Simulator application in standalone mode
 * @author Oleg Ciobanu ociobanu@tacitknowledge.com
 *
 */
public class StandAloneStarter extends Thread {
	public static final String CLOSE_PORT_NAME = "closePort";
	public static final String CLOSE_COMMAND_NAME = "closeCommand";
	public static final String DEFAULT_CLOSE_COMMAND = "CLOSE";
	public static final int DEFAULT_CLOSE_PORT = 8080;
	
	/**
	 * listenPort - port on which application will expect close command
	 */
	int closePort = DEFAULT_CLOSE_PORT;
	/**
	 * stopCommand - is the command that will close the application.
	 */
	String closeCommand = DEFAULT_CLOSE_COMMAND;
	/**
	 * 
	 */
	ConversationManager manager;
	
	private static Logger logger = LoggerFactory.getLogger(StandAloneStarter.class);
	
	StandAloneStarter()
	{
		RouteManager routeManager = new RouteManagerImpl(); 
		manager = new ConversationManagerImpl(routeManager);
		loadConfiguration();
	}
	/**
	 * This method should include all steps required at the start of the application
	 */
	public void executeAtStart()
	{
		// all chores at the start of the application
	}
	/**
	 * This method to hold all steps before to close the application
	 */
	public void executeBeforeStop()
	{
		// all chores before to leave the application
	}
	/**
	 * Application entry point in stand alone mode
	 * @param args
	 */
	public static void main(String[] args)
	{
		StandAloneStarter ep = new StandAloneStarter();
		ep.start();
	}
	/**
	 * This method will wait the close command on the listening port
	 */
	private void waitCloseCommand(){
		ServerSocket socket = null;
		try {
			socket = new ServerSocket(closePort);
		} catch (IOException e) {
			// could not create listening socket
			if(logger.isErrorEnabled()){
				logger.error(e.getMessage());
			}
		}
		if (socket != null) {
			try {
				while(true){
					Socket incommingConnectionNewSocket = socket.accept();
					InputStreamReader reader = new InputStreamReader(incommingConnectionNewSocket.getInputStream());
					StringBuilder readString = new StringBuilder();
					char buffer[] = new char[10];
					int read = 0;
					while ((read = reader.read(buffer))!=-1 && readString.length() < closeCommand.length()) {
						readString.append(buffer, 0, read);
					}
					
					incommingConnectionNewSocket.close();
					
					if (readString != null && readString.equals(closeCommand)) {
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
	 * Assembler method that starts the application,
	 * wait for close command on a certain port and 
	 * prepares(resources cleaning, etc.) the application before to leave it.
	 */
	public void run() {
		executeAtStart();
		waitCloseCommand();
		executeBeforeStop();
	}
	/**
	 * listening port getter
	 * @return listening port
	 */
	public int getListenPort() {
		return closePort;
	}
	/**
	 * listening port setter 
	 * @param port on which application will listen close command
	 */
	public void setListenPort(int listenPort) {
		this.closePort = listenPort;
	}
	/**
	 * stop command getter
	 * @return
	 */
	public String getStopCommand() {
		return closeCommand;
	}
	/**
	 * stop command setter
	 * @param stopWord
	 */
	public void setStopCommand(String stopWord) {
		this.closeCommand = stopWord;
	}
	/**
	 * Load configured close port and close command.
	 */
	private void loadConfiguration()
	{
		ResourceBundle bundle = ResourceBundle.getBundle("config");
		if(bundle.containsKey(CLOSE_PORT_NAME))
		{
			this.closePort = Integer.parseInt(bundle.getString(CLOSE_PORT_NAME));
		}
		
		if(bundle.containsKey(CLOSE_COMMAND_NAME)){
			this.closeCommand = bundle.getString(CLOSE_COMMAND_NAME);
		}
	}
}
