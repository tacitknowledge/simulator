package com.tacitknowledge.simulator.formats;

import com.tacitknowledge.simulator.FormatAdapterException;
import org.junit.Test;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertEquals;

import java.util.HashMap;
import java.util.Map;

import static junit.framework.Assert.assertNull;

/**
 * Test class for RestAdapterTest
 *
 * @author Raul Huerta (rhuerta@tacitknowledge.com)
 */
public class RestAdapterTest {

    private RestAdapter adapter;

    public void setUp()
    {
        adapter = (RestAdapter) AdapterFactory.getInstance().getAdapter(FormatConstants.REST);
    }

    public void testCreateSimulatorPojo() throws FormatAdapterException {
        //TODO in progress
    }

    @Test
    public void testUrlPatternExtract(){
        String url = "/something/2/another/4";
        String pattern = "/something/:something_id/another/:another_id";

        Map<String, Object> parameterMap = extractValuesFromUrl(url, pattern);
        assertNotNull("property something_id does not exist", parameterMap.get("something_id"));
        assertEquals("2", parameterMap.get("something_id"));
        assertNotNull("property another_id does not exist", parameterMap.get("another_id"));
        assertEquals("4", parameterMap.get("another_id"));

        String url2 = "/something/1";
        String pattern2 = "/something/:something_id/another/:another_id";

        parameterMap = extractValuesFromUrl(url2, pattern2);
        assertNotNull("parametermap must not be null", parameterMap);
        assertNotNull("property something_id does not exist", parameterMap.get("something_id"));
        assertEquals("1", parameterMap.get("something_id"));
        assertNull("property another_id should not exist", parameterMap.get("another_id"));

        String url3 = "/something/198/anothething/yeah";
        String pattern3 = "/something/:something_id";

        parameterMap = extractValuesFromUrl(url3, pattern3);
        assertNotNull("parametermap must not be null", parameterMap);
        assertNotNull("property something_id does not exist", parameterMap.get("something_id"));
        assertEquals("198", parameterMap.get("something_id"));
        assertEquals(1, parameterMap.size());

    }



    private Map<String, Object> extractValuesFromUrl(String url, String pattern) {
        String[] patternArr = pattern.split("/");
        String[] urlArr = url.split("/");
        Map<String, Object> parameterMap = new HashMap<String, Object>();

        int patternArrLength = patternArr.length;
        for(int i = 0; i < patternArrLength ; i ++){
            String str = patternArr[i];
            if(str.startsWith(":")){
                try{
                    parameterMap.put(str.substring(1), urlArr[i]);
                }catch(ArrayIndexOutOfBoundsException e){
                    //This is fine, since it means that the url is shorter that the pattern.
                    //We just swallow the exception and continue.
                }
            }
        }

        return parameterMap;  //To change body of created methods use File | Settings | File Templates.
    }

}