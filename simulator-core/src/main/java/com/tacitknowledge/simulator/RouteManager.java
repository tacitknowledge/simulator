package com.tacitknowledge.simulator;

/**
 * Defines the interface for the RouteManager Implementations
 * @author Alexandru Dereveanco (adereveanco@tacitknowledge.com)
 *
 */
public interface RouteManager
{

    /**
     * Implementaion of route builder configure
     *
     * @throws Exception in case of an error
     */
    void configure() throws Exception;

    /**
     * Builds simulation route using conversation object. Assigns adapter beans to the route,
     * assigns simulation execution bean to the route. Adds route to the current camel context.
     *
     * @param conversation object to be used in the route.
     * @throws Exception in case of an error
     */
    void activate(Conversation conversation) throws Exception;

    /**
     * Stops the camel route without removing it from the context.
     *
     * @param conversation object to be used in the route.
     * @throws Exception in case of an error.
     */
    void deactivate(Conversation conversation) throws Exception;

    /**
     *
     * @param conversation
     * @return
     */
    boolean isActive(Conversation conversation) throws SimulatorException;

    /**
     *
     * @param conversation
     */
    void delete(Conversation conversation) throws Exception;

}
