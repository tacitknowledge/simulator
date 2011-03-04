package com.tacitknowledge.simulator;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.ResourceBundle;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StandAloneStopper {
	/**
	 * host name on which the application will be running
	 */
	private static final String LOCALHOST = "localhost";
	/**
	 * 
	 */
	int closePort = StandAloneStarter.DEFAULT_CLOSE_PORT;
	String closeCommand = StandAloneStarter.DEFAULT_CLOSE_COMMAND;
	/**
	 * class logger
	 */
	private static Logger logger = LoggerFactory.getLogger(StandAloneStopper.class);
	/**
	 * constructor
	 */
	public StandAloneStopper(){
		loadConfiguration();
	}
	/**
	 * Load configured close port and close command.
	 */
	private void loadConfiguration()
	{
		ResourceBundle bundle = ResourceBundle.getBundle("config");
		if(bundle.containsKey(StandAloneStarter.CLOSE_PORT_NAME))
		{
			this.closePort = Integer.parseInt(bundle.getString(StandAloneStarter.CLOSE_PORT_NAME));
		}
		
		if(bundle.containsKey(StandAloneStarter.CLOSE_COMMAND_NAME))
		{
			this.closeCommand = bundle.getString(StandAloneStarter.CLOSE_COMMAND_NAME);
		}
	}
	/**
	 * send close command on the close port
	 */
	public void sendCloseCommand()
	{
		Socket socket = new Socket();
		try
		{
			socket.connect(new InetSocketAddress(LOCALHOST, closePort));
			OutputStreamWriter writer = new OutputStreamWriter(socket.getOutputStream());
			writer.write(closeCommand);
			writer.flush();
			writer.close();
		}
		catch (IOException e) 
		{
			logger.error(e.getMessage());
		}
		finally
		{
			if(socket.isConnected())
			{
				try
				{
					socket.close();
				}
				catch(IOException exception)
				{
					if (logger.isErrorEnabled()) 
					{
						logger.error(exception.getMessage());
					}
				}
			}
		}
	}
	/**
	 * entry point for the program that will stop the simulator running in standalone mode.
	 * @param args
	 */
	public static void main(String[] args)
	{
		StandAloneStopper stopper = new StandAloneStopper();
		stopper.sendCloseCommand();
	}
}
