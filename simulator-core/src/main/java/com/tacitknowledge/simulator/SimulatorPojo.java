package com.tacitknowledge.simulator;

import java.util.Map;

/**
 * @author Jorge Galindo (jgalindo@tacitknowledge.com)
 */
public interface SimulatorPojo
{
    public Map getRoot();
    public void setRoot(Map root);
    public Object getAttribute(String name);
}
