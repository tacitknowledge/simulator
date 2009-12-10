package com.tacitknowledge.simulator.formats;

import com.tacitknowledge.simulator.Adapter;
import com.tacitknowledge.simulator.FormatAdapterException;
import com.tacitknowledge.simulator.SimulatorPojo;
import com.tacitknowledge.simulator.StructuredSimulatorPojo;

import static com.tacitknowledge.simulator.configuration.ParameterDefinitionBuilder.parameter;
import static com.tacitknowledge.simulator.configuration.ParametersListBuilder.parameters;

import com.tacitknowledge.simulator.configuration.ParameterDefinitionBuilder;
import org.apache.log4j.Logger;
import org.ho.yaml.YamlDecoder;
import org.ho.yaml.YamlEncoder;

import java.io.ByteArrayOutputStream;
import java.io.EOFException;
import java.io.OutputStream;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Implementation of the Adapter interface for the YAML format
 *
 * @author Jorge Galindo (jgalindo@tacitknowledge.com)
 */
public class YamlAdapter extends BaseAdapter implements Adapter<Object>
{
    /**
     * String describing what are the YAML contents. REQUIRED.
     * This will be used as the SimulatorPojo root's record key.
     * e.g.: employees, orders, products, etc.
     */
    public static final String PARAM_YAML_CONTENT = "yamlContent";

    /**
     * Is Array parameter. Determines if the YAML content is an Array. OPTIONAL.
     * Defaults to false.
     * If this parameter is true, it's recommended that #PARAM_YAML_CONTENT uses a plural
     * word and #PARAM_YAML_ARRAY_CONTENT its singular form.
     */
    public static final String PARAM_IS_ARRAY = "isArray";

    /**
     * JSON array content parameter. Describes each array element content. OPTIONAL.
     * Required if #PARAM_IS_ARRAY is true.
     * e.g.: employee, order, product, etc.
     */
    public static final String PARAM_YAML_ARRAY_CONTENT = "jsonArrayContent";

    
    /**
     * Adapter parameters definition.
     */
    private static List<ParameterDefinitionBuilder.ParameterDefinition> parametersList =
        parameters().
            add(parameter().
                name(PARAM_YAML_CONTENT).
                label("YAML Contents (e.g. employees, orders, etc.)").
                required(true)
            ).
            add(parameter().
                name(PARAM_IS_ARRAY).
                label("Is YAML content an array?").
                type(ParameterDefinitionBuilder.ParameterDefinition.TYPE_BOOLEAN)
            ).
            add(parameter().
                name(PARAM_YAML_ARRAY_CONTENT).
                label("YAML Array content (What each array element represents. " +
                            "e.g.: employee, order. Required if content is array)")
            ).
        build();

    /**
     * Logger for this class.
     */
    private static Logger logger = Logger.getLogger(YamlAdapter.class);

    /**
     * @see #PARAM_IS_ARRAY
     */
    private boolean isArray = false;

    /**
     * @inheritDoc
     */
    public YamlAdapter()
    {
    }

    /**
     * @inheritDoc
     * @param parameters @see Adapter#parameters
     */
    public YamlAdapter(Map<String, String> parameters)
    {
        super(parameters);
    }

    protected SimulatorPojo createSimulatorPojo(String object)
            throws FormatAdapterException
    {
        SimulatorPojo pojo = new StructuredSimulatorPojo();

        YamlDecoder enc = new YamlDecoder(new StringReader(object));

        try
        {
            Object yaml = enc.readObject();

            if (this.isArray)
            {
                Map<String, Object> map = new HashMap<String, Object>();
                map.put(getParamValue(PARAM_YAML_ARRAY_CONTENT), yaml);
                pojo.getRoot().put(getParamValue(PARAM_YAML_CONTENT), map);
            }
            else
            {
                pojo.getRoot().put(getParamValue(PARAM_YAML_CONTENT), yaml);
            }
        }
        catch(EOFException e)
        {
            logger.error("Unexpected error trying to read YAML object: " + e.getMessage());
            throw new FormatAdapterException(e.getMessage(), e);
        }
        return pojo;
    }

    @Override
    protected String getString(SimulatorPojo scriptExecutionResult) throws FormatAdapterException
    {
        //todo implement
        return null;
    }


    /**
     * @inheritDoc
     * @param pojo @see Adapter#adaptTo
     * @return @see Adapter#adaptTo
     * @throws FormatAdapterException @see Adapter#adaptTo
     */
    public String adaptTo(SimulatorPojo pojo) throws FormatAdapterException
    {
        validateParameters();

        // --- Only one entry in the root should exist
        if (pojo.getRoot().isEmpty() || pojo.getRoot().size() > 1)
        {
            String errorMsg = "SimulatorPojo's root should contain only one Entry for YAML adaptTo";
            logger.error(errorMsg);
            throw new FormatAdapterException(errorMsg);
        }

        OutputStream os = new ByteArrayOutputStream();
        YamlEncoder enc = new YamlEncoder(os);

        enc.writeObject(pojo.getRoot().get(getParamValue(PARAM_YAML_CONTENT)));
        enc.close();

        return os.toString();
    }

    /**
     * @inheritDoc
     * @throws FormatAdapterException if any required parameter is missing
     */
    @Override
    void validateParameters() throws FormatAdapterException
    {
        if (getParamValue(PARAM_IS_ARRAY) != null)
        {
            this.isArray = Boolean.parseBoolean(getParamValue(PARAM_IS_ARRAY));
        }

        if (getParamValue(PARAM_YAML_CONTENT) == null)
        {
            throw new FormatAdapterException("YAML content is required");
        }
        if (this.isArray && getParamValue(PARAM_YAML_ARRAY_CONTENT) == null)
        {
            throw new FormatAdapterException(
                    "YAML Array Content parameter is required if YAML content is an array");
        }
    }

    public List<List> getParametersList()
    {
        return getParametersDefinitionsAsList(parametersList);
    }
}
