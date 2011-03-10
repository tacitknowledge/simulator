package com.tacitknowledge.simulator.standalone;

import java.util.ResourceBundle;

import com.tacitknowledge.simulator.utils.Configuration;

/**
 * This class contains the logic of loading the conversations
 *  with a certain frequency.
 * @author Oleg Ciobanu ociobanu@tacitknowledge.com
 *
 */
public class ConversationsScheduledLoader extends Thread {

    /**
     *  stop flag.
     */
    private Boolean toStop = Boolean.FALSE;

    /**
     * The constructor will load from config files all interested configs.
     */
    public ConversationsScheduledLoader()
    {}

    /**
    * toStop getter.
    * @return toStop flag
    */
    public synchronized Boolean getToStop()
    {
        return toStop;
    }

    /**
     * toStop setter.
     * @param toStop
     */
    public synchronized void setToStop(boolean toStop)
    {
        this.toStop = toStop;
    }

    /**
     * conversations scheduled loader will load configurations
     * from the the files with a configured frequency.
     */
    @Override
    public void run()
    {
        while (!getToStop())
        {
            // TODO
            // reload conversations from files every
            // conversationReadingFrequency milliseconds

            //wait a certain amount of time
            synchronized (this)
            {
                try
                {
                    Thread.currentThread()
                            .wait(Configuration
                                    .getPropertyAsInt(Configuration.CONVERSATIONS_READING_FREQUENCY_NAME));
                }
                catch (InterruptedException e)
                {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }
    }
}
