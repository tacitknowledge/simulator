package com.tacitknowledge.simulator.adapters;

import com.tacitknowledge.simulator.pojos.SimulatorPojo;

/**
 * A format converter.
 * @author Galo
 */
public interface Adapter<E> {
    SimulatorPojo adaptFrom(E e);

    E adaptTo(SimulatorPojo pojo);
}
