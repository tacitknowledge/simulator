package com.tacitknowledge.simulator.scripting;

import com.tacitknowledge.simulator.SimulatorException;

import java.util.Map;

/**
 * Date: 07.12.2009
 * Time: 18:11:08
 *
 * @author Nikita Belenkiy (nbelenkiy@tacitknowledge.com)
 */
public interface ObjectMapper {
    
    Map<String, Object> getMapFromObject(Object o) throws SimulatorException, ObjectMapperException;
}
