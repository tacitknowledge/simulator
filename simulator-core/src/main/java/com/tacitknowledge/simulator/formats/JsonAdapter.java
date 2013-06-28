package com.tacitknowledge.simulator.formats;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.tacitknowledge.simulator.scripting.ObjectMapperException;
import com.tacitknowledge.simulator.scripting.SimulatorPojoPopulatorImpl;
import org.apache.camel.Exchange;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tacitknowledge.simulator.Adapter;
import com.tacitknowledge.simulator.ConfigurableException;
import com.tacitknowledge.simulator.FormatAdapterException;
import com.tacitknowledge.simulator.SimulatorPojo;
import com.tacitknowledge.simulator.StructuredSimulatorPojo;

/**
 * Implementation of the Adapter interface for the JSON format
 *
 * @author Jorge Galindo (jgalindo@tacitknowledge.com)
 */
public class JsonAdapter extends NativeObjectScriptingAdapter implements Adapter
{
    /**
     * JSON content parameter. Describes what are the JSON contents. REQUIRED.
     * This will be used as the SimulatorPojo root's record key.
     * e.g.: employee(s), order(s), product(s), etc.
     */
    public static final String PARAM_JSON_CONTENT = "jsonContent";

    /**
     * Is Array parameter. Determines if the JSON content is an Array. OPTIONAL.
     * Defaults to false (JSON Object).
     * If this parameter is true, it's recommended that #PARAM_JSON_CONTENT uses a plural
     * word and #PARAM_JSON_ARRAY_CONTENT its singular form.
     */
    public static final String PARAM_IS_ARRAY = "isArray";

    /**
     * JSON array content parameter. Describes each array element content. OPTIONAL.
     * Required if #PARAM_IS_ARRAY is true.
     * e.g.: employee, order, product, etc.
     */
    public static final String PARAM_JSON_ARRAY_CONTENT = "jsonArrayContent";

    /**
     * Logger for this class.
     */
    private static Logger logger = LoggerFactory.getLogger(JsonAdapter.class);

    /**
     * @see #PARAM_IS_ARRAY
     */
    private boolean isArray = false;

    /**
     * Constructor
     */
    public JsonAdapter()
    {
    }

    /**
     * Constructor
     *
     * @param bound Configurable bound
     * @param parameters @see Adapter#parameters
     * @inheritDoc
     */
    public JsonAdapter(final int bound, final Map<String, String> parameters)
    {
        super(bound, parameters);
    }
    protected SimulatorPojo getSimulatorPojo(final Object object) throws ObjectMapperException
    {
        final SimulatorPojo payload = SimulatorPojoPopulatorImpl.getInstance().populateSimulatorPojoFromBean(object,
                getParamValue(PARAM_JSON_CONTENT));
        return payload;
    }

    /**
     * Creates a simuator pojo
     *
     * @param exchange incoming data
     * @return simulator pojo
     * @throws FormatAdapterException if format adapter error occurs
     */
    @Override
    protected SimulatorPojo createSimulatorPojo(final Exchange exchange)
        throws FormatAdapterException
    {

        String o = exchange.getIn().getBody(String.class);

        logger.debug("Attempting to generate SimulatorPojo from JSON content:\n{}", o);

        SimulatorPojo pojo = new StructuredSimulatorPojo();

        try
        {
            if (this.isArray)
            {
                logger.debug("Expecting JSON array in content. Processing as such.");

                // --- If the JSON content is an array, build the root Map a little different
                Map<String, Object> mapFromJsonList = new HashMap<String, Object>();
                mapFromJsonList.put(
                    getParamValue(PARAM_JSON_ARRAY_CONTENT),
                    getListFomJsonArray(new JSONArray(o))
                );

                pojo.getRoot().put(
                    getParamValue(PARAM_JSON_CONTENT),
                    mapFromJsonList
                );
            }
            else
            {
                pojo.getRoot().put(
                    getParamValue(PARAM_JSON_CONTENT),
                    getMapFromJsonObj(new JSONObject(o)));
            }
        }
        catch (JSONException je)
        {
            String errorMsg =
                "Unexpected error trying to parse String into JSON: ";
            throw new FormatAdapterException(errorMsg, je);
        }

        logger.debug("Finished generating SimulatorPojo from JSON content");
        return pojo;
    }

    /**
     * Gets String value from simulator pojo
     *
     * @param simulatorPojo the simulator pojo
     * @param exchange The Camel Exchange
     * @return the string value
     * @throws FormatAdapterException if a format adapter error occurs
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Override
    protected String getString(final SimulatorPojo simulatorPojo, final Exchange exchange)
        throws FormatAdapterException
    {
        String jsonString;

        // --- The SimulatorPojo for JSONAdapter should contain only one key in its root
        if (simulatorPojo.getRoot().isEmpty() || simulatorPojo.getRoot().size() > 1)
        {
            throw new
                FormatAdapterException(
                "Incorrect SimulatorPojo's root size. Expecting 1, but found"
                    + simulatorPojo.getRoot().size());
        }

        // --- The pojo's root should only contain an entry with key PARAM_JSON_CONTENT
        Map<String, Object> map =
            (Map<String, Object>) simulatorPojo.getRoot().get(getParamValue(PARAM_JSON_CONTENT));

        // --- If the JSON content is an array...
        if (this.isArray)
        {
            // ...generate a JSONArray from the pojo's root entry
            // (key should be #PARAM_JSON_ARRAY_CONTENT)
            List items = (List) map.get(getParamValue(PARAM_JSON_ARRAY_CONTENT));
            JSONArray jsonArray = new JSONArray(items);
            jsonString = jsonArray.toString();
        }
        else
        {
            // ...otherwise, go with the default JSONObject
            JSONObject jsonObject = new JSONObject(map);
            jsonString = jsonObject.toString();
        }

        return jsonString;
    }

    /**
     * Gets map from json object
     *
     * @param json the json object
     * @return the map
     * @throws FormatAdapterException if a format adapter error occurs
     */
    @SuppressWarnings("rawtypes")
    private Map<String, Object> getMapFromJsonObj(final JSONObject json)
        throws FormatAdapterException
    {
        if (json == null)
        {
            String errorMsg = "Null JSON object";
            throw new FormatAdapterException(errorMsg);
        }

        Map<String, Object> map = new HashMap<String, Object>();

        Iterator i = json.keys();
        while (i.hasNext())
        {
            String key = (String) i.next();
            try
            {
                Object value = json.get(key);
                Object mapValue = null;

                // --- If the value is...
                if (value instanceof JSONObject)
                {
                    // ...a JSON object, get its Map representation
                    mapValue = getMapFromJsonObj((JSONObject) value);
                }
                else if (value instanceof JSONArray)
                {
                    // ...a JSONArray, get its List representation
                    mapValue = getListFomJsonArray((JSONArray) value);
                }
                else
                {
                    // ...anything else, treat is as a String
                    mapValue = value.toString();
                }

                map.put(key, mapValue);
            }
            catch (JSONException je)
            {
                String errorMsg =
                    "Unexpected error trying to get value JSON object: " + je.getMessage();
                throw new FormatAdapterException(errorMsg, je);
            }

        }

        return map;
    }

    /**
     * Gets list from Json Array
     *
     * @param array - the json array
     * @return list
     * @throws FormatAdapterException if a format adapter problem occurs
     */
    @SuppressWarnings({ "rawtypes", "unchecked" })
    private List getListFomJsonArray(final JSONArray array) throws FormatAdapterException
    {
        if (array == null)
        {
            String errorMsg = "Null JSON array";
            throw new FormatAdapterException(errorMsg);
        }

        List list = new ArrayList();

        for (int i = 0; i < array.length(); i++)
        {
            try
            {
                Object item = array.get(i);
                Object listValue = null;

                // --- If the item is...
                if (item instanceof JSONObject)
                {
                    // ...a JSON object, get its Map representation
                    listValue = getMapFromJsonObj((JSONObject) item);
                }
                else if (item instanceof JSONArray)
                {
                    // ...a JSONArray, get its List representation
                    listValue = getListFomJsonArray((JSONArray) item);
                }
                else
                {
                    // ...anything else, treat is as a String
                    listValue = item.toString();
                }
                list.add(listValue);
            }
            catch (JSONException je)
            {
                String errorMsg =
                    "Unexpected error trying to get value JSON object: ";
                throw new FormatAdapterException(errorMsg, je);
            }
        }
        return list;
    }

    /**
     * Validates parameters
     *
     * @throws ConfigurableException If a required parameter is missing
     * @inheritDoc
     */
    @Override
    protected void validateParameters() throws ConfigurableException
    {
        if (getParamValue(PARAM_IS_ARRAY) != null)
        {
            this.isArray = Boolean.parseBoolean(getParamValue(PARAM_IS_ARRAY));
        }

        if (getParamValue(PARAM_JSON_CONTENT) == null)
        {
            throw new ConfigurableException("JSON Content parameter is required.");
        }
        // --- If JSON content is array, PARAM_JSON_ARRAY_CONTENT is required
        if (this.isArray && getParamValue(PARAM_IS_ARRAY) == null)
        {
            throw new ConfigurableException(
                "JSON Array Content parameter is required if JSON content is an array");
        }
    }
}
