package com.tacitknowledge.simulator.standalone;

import java.io.IOException;

import junit.framework.Assert;

import org.junit.Test;
/**
 * Test that a close command is detected on the close port and this is detected by ConversationsScheduledLoader  
 * @author Oleg Ciobanu ociobanu@tacitknowledge.com
 */
public class StandAloneTest {
	@Test
	public void test()
	{
		try
		{
			// initialize the "conversations scheduled loader"  
			ConversationsScheduledLoader loader = new ConversationsScheduledLoader();
			// initialize a close command waiter.
			CloseCommandWaiter closeWaiter = new CloseCommandWaiter();
			
			closeWaiter.setConversationsScheduledLoader(loader) ;
			
			// start "file loader" thread and "close command waiter" thread
			loader.start();
			closeWaiter.start();
			
			// send close command
			StandAloneStopper stoper = new StandAloneStopper();
			stoper.sendCloseCommand();
			try
			{
				// wait the close command propagation through socket. 
				Thread.currentThread().sleep(500);
			}
			catch (InterruptedException e)
			{
			}
			// assert the close command was detected.
			Assert.assertEquals(Boolean.TRUE, loader.getToStop());
		}
		catch (IOException e)
		{
			Assert.fail(e.getMessage());
		}
	}
}
