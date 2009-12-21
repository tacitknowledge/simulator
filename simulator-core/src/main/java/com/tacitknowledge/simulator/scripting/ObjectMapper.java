package com.tacitknowledge.simulator.scripting;

import java.util.Map;

/**
 * Object mapper interface
 *
 * @author Nikita Belenkiy (nbelenkiy@tacitknowledge.com)
 */
public interface ObjectMapper
{
    /**
     * Returns a Map from an object. Each attribute in the object will become a key in the Map.
     *
     * @param o The object to be mapped
     * @return The map representation of the passed object
     * @throws ObjectMapperException If anything goes wrong
     */
    Map<String, Object> getMapFromObject(Object o) throws ObjectMapperException;
}
