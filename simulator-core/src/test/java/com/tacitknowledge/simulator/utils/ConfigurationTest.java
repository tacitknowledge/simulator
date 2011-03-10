package com.tacitknowledge.simulator.utils;

import org.junit.Assert;
import org.junit.Test;

public class ConfigurationTest
{
    /**
     * Test loading of an unknown property.
     */
    @Test
    public void testLoadingAnUnknownProperty() {
        Assert.assertNull(Configuration.getDefaultValue("UnknownPropertyName"));
    }
    /**
     * Test loading of a known configured property.
     */
    @Test
    public void testLoadingAKnownProperty() {
        Assert.assertNotNull(Configuration.getPropertyAsString(Configuration.CLOSE_COMMAND_NAME));
    }
    
}
