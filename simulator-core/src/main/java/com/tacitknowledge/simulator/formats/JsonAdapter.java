package com.tacitknowledge.simulator.formats;

import com.tacitknowledge.simulator.Adapter;
import com.tacitknowledge.simulator.FormatAdapterException;
import com.tacitknowledge.simulator.SimulatorPojo;
import com.tacitknowledge.simulator.StructuredSimulatorPojo;
import com.tacitknowledge.simulator.configuration.ParameterDefinitionBuilder;
import static com.tacitknowledge.simulator.configuration.ParameterDefinitionBuilder.name;
import static com.tacitknowledge.simulator.configuration.ParametersListBuilder.parameters;
import org.apache.log4j.Logger;
import org.apache.camel.Exchange;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

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
    private List<ParameterDefinitionBuilder.ParameterDefinition> parametersList =
        parameters().
            add(
                name(PARAM_JSON_CONTENT).
                    label("JSON Contents (e.g. employee(s), order(s), etc.)").
                    required()
            ).
            add(
                name(PARAM_IS_ARRAY).
                    label("Is JSON content an array? e.g. [ ... ]").
                    type(ParameterDefinitionBuilder.ParameterDefinition.TYPE_BOOLEAN)
            ).
            add(
                name(PARAM_JSON_ARRAY_CONTENT).
                    label("JSON Array content (What each array element represents. "
                    +
                    "e.g.: employee, order. Required if content is array)")
            );

    /**
     * Logger for this class.
     */
    private static Logger logger = Logger.getLogger(JsonAdapter.class);

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
     * @param parameters @see Adapter#parameters
     * @inheritDoc
     */
    public JsonAdapter(final Map<String, String> parameters)
    {
        super(parameters);
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

        logger.debug("Attempting to generate SimulatorPojo from JSON content:\n" + o);

        SimulatorPojo pojo = new StructuredSimulatorPojo();

        try
        {
            //JSONObject json = new JSONObject(o);

            //pojo.getRoot().put(getParamValue(PARAM_JSON_CONTENT), getMapFromJsonObj(json));
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
                "Unexpected error trying to parse String into JSON: " + je.getMessage();
            logger.error(errorMsg, je);
            throw new FormatAdapterException(errorMsg, je);
        }

        logger.debug("Finished generating SimulatorPojo from JSON content");
        return pojo;
    }

    /**
     * Gets String value from simulator pojo
     *
     * @param simulatorPojo the simulator pojo
     * @return the string value
     * @throws FormatAdapterException if a format adapter error occurs
     */
    @Override
    protected Object getString(final SimulatorPojo simulatorPojo, Exchange exchange) throws FormatAdapterException
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
     * Gets map from json object
     *
     * @param json the json object
     * @return the map
     * @throws FormatAdapterException if a format adapter error occurs
     */
    private Map<String, Object> getMapFromJsonObj(final JSONObject json)
        throws FormatAdapterException
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

    /**
     * Gets list from Json Array
     *
     * @param array - the json array
     * @return list
     * @throws FormatAdapterException if a format adapter problem occurs
     */
    private List getListFomJsonArray(final JSONArray array) throws FormatAdapterException
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
     * Validates parameters
     *
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

    /**
     * Gets parameters list
     *
     * @return list of parameters
     */
    public List<List> getParametersList()
    {
        return getParametersDefinitionsAsList(parametersList);
    }
}
