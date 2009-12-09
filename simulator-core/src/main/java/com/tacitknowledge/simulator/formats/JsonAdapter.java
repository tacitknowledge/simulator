package com.tacitknowledge.simulator.formats;

import com.tacitknowledge.simulator.Adapter;
import com.tacitknowledge.simulator.FormatAdapterException;
import com.tacitknowledge.simulator.SimulatorPojo;
import com.tacitknowledge.simulator.StructuredSimulatorPojo;
import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.*;

/**
 * Implementation of the Adapter interface for the JSON format
 *
 * @author Jorge Galindo (jgalindo@tacitknowledge.com)
 */
public class JsonAdapter extends BaseAdapter implements Adapter<Object>
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
     * Adapter parameters definition.
     */
    private List<List> parametersList = new ArrayList<List>()
    {
        {
            add(new ArrayList<String>()
            {
                {
                    add(PARAM_JSON_CONTENT);
                    add("JSON Contents (e.g. employee(s), order(s), etc.)");
                    add("string");
                    add("required");
                }
            });

            add(new ArrayList<String>()
            {
                {
                    add(PARAM_IS_ARRAY);
                    add("Is JSON content an array? e.g. [ ... ]");
                    add("boolean");
                    add("optional");
                }
            });

            add(new ArrayList<String>()
            {
                {
                    add(PARAM_JSON_ARRAY_CONTENT);
                    add("JSON Array content (What each array element represents. " +
                            "e.g.: employee, order. Required if content is array)");
                    add("string");
                    add("optional");
                }
            });
        }
    };

    /**
     * Logger for this class.
     */
    private static Logger logger = Logger.getLogger(JsonAdapter.class);

    /**
     * @see #PARAM_IS_ARRAY
     */
    private boolean isArray = false;

    public JsonAdapter()
    {
    }

    /**
     * @param parameters @see Adapter#parameters
     * @inheritDoc
     */
    public JsonAdapter(Map<String, String> parameters)
    {
        super(parameters);
    }

    protected SimulatorPojo createSimulatorPojo(String o)
            throws FormatAdapterException
    {
        SimulatorPojo pojo = new StructuredSimulatorPojo();

        try
        {
            //JSONObject json = new JSONObject(o);

            //pojo.getRoot().put(getParamValue(PARAM_JSON_CONTENT), getMapFromJsonObj(json));
            if (this.isArray)
            {
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
                    "Unexpected error trying to parse String into JSON: " + je.getMessage();
            logger.error(errorMsg, je);
            throw new FormatAdapterException(errorMsg, je);
        }
        return pojo;
    }


    protected String getString(SimulatorPojo simulatorPojo)
            throws FormatAdapterException
    {
        // --- The SimulatorPojo for JSONAdapter should contain only one key in its root
        if (simulatorPojo.getRoot().isEmpty() || simulatorPojo.getRoot().size() > 1)
        {
            logger.error("  Incorrect SimulatorPojo's root size. Expecting 1, but found"
                    + simulatorPojo.getRoot().size());
            throw new
                    FormatAdapterException(
                    "Incorrect SimulatorPojo's root size. Expecting 1, but found"
                            + simulatorPojo.getRoot().size());
        }

        // --- The pojo's root should only contain an entry with key PARAM_JSON_CONTENT
        Map<String, Object> map =
                (Map<String, Object>) simulatorPojo.getRoot().get(getParamValue(PARAM_JSON_CONTENT));

        String jsonString1;

        // --- If the JSON content is an array...
        if (this.isArray)
        {
            // ...generate a JSONArray from the pojo's root entry
            // (key should be #PARAM_JSON_ARRAY_CONTENT)
            List items = (List) map.get(getParamValue(PARAM_JSON_ARRAY_CONTENT));
            JSONArray jsonArray = new JSONArray(items);
            jsonString1 = jsonArray.toString();
        }
        else
        {
            // ...otherwise, go with the default JSONObject
            JSONObject jsonObject = new JSONObject(map);
            jsonString1 = jsonObject.toString();
        }
        String jsonString = jsonString1;
        return jsonString;
    }

    /**
     * @return @see Adapter#getParametersList
     * @inheritDoc
     */
    public List<List> getParametersList()
    {
        return parametersList;
    }

    private Map<String, Object> getMapFromJsonObj(JSONObject json) throws FormatAdapterException
    {
        if (json == null)
        {
            String errorMsg = "Null JSON object";
            logger.error(errorMsg);
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
                logger.error(errorMsg, je);
                throw new FormatAdapterException(errorMsg, je);
            }

        }

        return map;
    }

    private List getListFomJsonArray(JSONArray array) throws FormatAdapterException
    {
        if (array == null)
        {
            String errorMsg = "Null JSON array";
            logger.error(errorMsg);
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
                        "Unexpected error trying to get value JSON object: " + je.getMessage();
                logger.error(errorMsg, je);
                throw new FormatAdapterException(errorMsg, je);
            }
        }
        return list;
    }

    /**
     * @throws FormatAdapterException If a required parameter is missing or not properly formatted
     * @inheritDoc
     */
    @Override
    void validateParameters() throws FormatAdapterException
    {
        if (getParamValue(PARAM_IS_ARRAY) != null)
        {
            this.isArray = Boolean.parseBoolean(getParamValue(PARAM_IS_ARRAY));
        }

        if (getParamValue(PARAM_JSON_CONTENT) == null)
        {
            throw new FormatAdapterException("JSON Content parameter is required.");
        }
        // --- If JSON content is array, PARAM_JSON_ARRAY_CONTENT is required
        if (this.isArray && getParamValue(PARAM_IS_ARRAY) == null)
        {
            throw new FormatAdapterException(
                    "JSON Array Content parameter is required if JSON content is an array");
        }
    }
}
