package com.tacitknowledge.simulator;

/**
 * A format converter.
 *
 * @author Jorge Galindo (jgalindo@tacitknowledge.com)
 */
public interface Adapter<E>
{
    SimulatorPojo adaptFrom(E e);

    E adaptTo(SimulatorPojo pojo);
}
