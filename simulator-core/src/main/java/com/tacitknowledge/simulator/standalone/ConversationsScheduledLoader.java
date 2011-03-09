package com.tacitknowledge.simulator.standalone;

import java.util.ResourceBundle;

/**
 * This class contains the logic of loading the conversations
 *  with a certain frequency.
 * @author Oleg Ciobanu ociobanu@tacitknowledge.com
 *
 */
public class ConversationsScheduledLoader extends Thread {
    private static final int DEFAULT_READING_FREQUENCY = 10000;

    /**
     * Property name for reading frequency.
     */
    private static final String CONVERSATIONS_READING_FREQUENCY =
        "conversationsReadingFrequency";

    /**
     *  stop flag.
     */
    private Boolean toStop = Boolean.FALSE;

    /**
     * The constructor will load from config files all interested configs.
     */
    public ConversationsScheduledLoader() {
        loadConfiguration();
    }
    /**
     * Conversations loading frequency.
     */
    private long conversationReadingFrequency = DEFAULT_READING_FREQUENCY;

    /**
     * toStop getter.
     * @return toStop flag
     */
    public synchronized Boolean getToStop() {
        return toStop;
    }

    /**
     * toStop setter.
     * @param toStop
     */
    public synchronized void setToStop(boolean toStop) {
        this.toStop = toStop;
    }

    /**
     * Load configured close port and close command.
     */
    private void loadConfiguration() {
        ResourceBundle bundle =
            ResourceBundle.getBundle(CloseCommandWaiter.CONFIG);
        if (bundle.containsKey(CONVERSATIONS_READING_FREQUENCY)) {
            this.conversationReadingFrequency = Integer.parseInt(bundle
                    .getString(CONVERSATIONS_READING_FREQUENCY));
        }
    }

    /**
     * conversations scheduled loader will load configurations
     * from the the files with a configured frequency.
     */
    @Override
    public void run() {
        while (!getToStop()) {
            // TODO
            // reload conversations from files every
            // conversationReadingFrequency milliseconds

            //wait a certain amount of time
            synchronized (this) {
                try {
                    Thread.currentThread().wait(conversationReadingFrequency);
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }
    }
}
