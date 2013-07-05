package com.tacitknowledge.simulator.camel;

import static org.junit.Assert.*;
import org.junit.Test;

/**
 * Created by IntelliJ IDEA.
 * User: mshort
 * Date: 7/5/13
 * Time: 9:19 AM
 * To change this template use File | Settings | File Templates.
 */
public class OneJarPackageScanResolverTest {


/*
file:/target/simulator-core.one-jar.jar!/lib/camel-spring-integration-2.6.0.jar!/org/apache/camel/component/spring/integration/converter/
target/simulator-core.one-jar.jar

     */
    @Test
    public void testExtractRelativePath() {
        String urlPath = "file:/target/simulator-core.one-jar.jar!/lib/camel-spring-integration-2.6.0.jar!/org/apache/camel/component/spring/integration/converter/";
        String expectedTopLevelPath = "target/simulator-core.one-jar.jar";
        assertEquals(expectedTopLevelPath,new OneJarPackageScanResolver().extractTopLevelPath(urlPath));

    }

    @Test
    public void testExtractAbsolutePath() {
        String urlPath = "file://target/simulator-core.one-jar.jar!/lib/camel-spring-integration-2.6.0.jar!/org/apache/camel/component/spring/integration/converter/";
        String expectedTopLevelPath = "/target/simulator-core.one-jar.jar";
        assertEquals(expectedTopLevelPath,new OneJarPackageScanResolver().extractTopLevelPath(urlPath));
    }

}
