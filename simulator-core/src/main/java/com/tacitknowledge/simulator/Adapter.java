package com.tacitknowledge.simulator;

/**
 * A format converter.
 * @author Galo
 */
public interface Adapter<E> {
    SimulatorPojo adaptFrom(E e);

    E adaptTo(SimulatorPojo pojo);
}
