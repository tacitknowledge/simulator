package com.tacitknowledge.simulator.configuration;

import java.util.ArrayList;
import java.util.List;

/**
 * DSL builder class for ParameterDefinitions.
 * A new parameter definition should be built in the following order:
 * - start with parameter()
 * - invoke the name and label required-attributes methods name().label()
 * - A ParameterDefinition object will be returned.
 * - Set the optional ParameterDefinition attributes (type, required, defaultValue)
 *
 * Example:
 *      parameter().
 *          name("newParameter").
 *          label("New Parameter").
 *          defaultValue("hello, World!");
 *
 * @see com.tacitknowledge.simulator.configuration.ParameterDefinitionBuilder.ParameterDefinition
 *
 * @author galo
 */
public class ParameterDefinitionBuilder
{
    /**
     * ParameterDefinition to be built
     */
    private final ParameterDefinition paramDef;

    /**
     * Hide default constructor
     */
    private ParameterDefinitionBuilder()
    {
        paramDef = new ParameterDefinition();
    }

    /**
     * This is the initial DSL command
     * @return The instance to work with
     */
    public static ParameterDefinitionBuilder parameter()
    {
        return new ParameterDefinitionBuilder();
    }

    /**
     * Required for successfully building the parameter definition.
     * @param name The parameter name
     * @return This instance
     */
    public ParameterDefinitionBuilder name(String name)
    {
        paramDef.setName(name);
        return this;
    }

    /**
     * Required for successfully building the parameter definition.
     * @param label The parameter field label
     * @return This instance
     */
    public ParameterDefinition label(String label)
    {
        paramDef.setLabel(label);
        return paramDef;
    }

    /**
     * Simulator Parameter definition.
     * Used by Format Adapters and Transports for defining what parameters
     * can/should be set for each implementation.
     * Can be created using the custom DSL defined in ParameterDefinitionBuilder only
     *
     * @see ParameterDefinitionBuilder
     * @author galo
     */
    public static class ParameterDefinition
    {
        /**
         * Parameter type of String constant
         */
        public static final String TYPE_STRING = "string";

        /**
         * Parameter type of boolean constant
         */
        public static final String TYPE_BOOLEAN = "boolean";

        /**
         * Parameter name. REQUIRED
         */
        private String name;

        /**
         * Parameter field label. REQUIRED.
         */
        private String label;

        /**
         * Parameter field type.
         * Defaults to String.
         */
        private String type = TYPE_STRING;

        /**
         * Flag to determine if the parameter is required.
         * Defaults to false.
         */
        private boolean required = false;

        /**
         * Default parameter value if none is provided.
         * If required is false, defaultValue should be provided.
         * Boolean type parameters should have their default value set in the corresponding
         * Adapter or Transport implementation. it would usually be false though.
         */
        private String defaultValue;

        private ParameterDefinition()
        {
        }

        /**
         *
         * @return name
         */
        public String getName()
        {
            return name;
        }

        /**
         *
         * @param name The parameter name.
         */
        private void setName(String name)
        {
            this.name = name;
        }

        /**
         *
         * @return label
         */
        public String getLabel()
        {
            return label;
        }

        /**
         *
         * @param label The parameter field label
         */
        private void setLabel(String label)
        {
            this.label = label;
        }

        /**
         * Returns the parameter type.
         * Values can be:
         *      - #TYPE_STRING
         *      - #TYPE_BOOLEAN
         * Defaults to #TYPE_STRING
         * @return type
         */
        public String getType()
        {
            return type;
        }

        /**
         *
         * @return isRequired
         */
        public boolean isRequired()
        {
            return required;
        }

        /**
         *
         * @return defaultValue
         */
        public String getDefaultValue()
        {
            return defaultValue;
        }


        /**
         * Sets the parameter type.
         * Must be one of:
         *  - ParameterDefinition.TYPE_STRING
         *  - ParameterDefinition.TYPE_BOOLEAN
         * If this attribute is not set, the type will be String by default.
         * @param type The parameter type.
         * @return This instance
         */
        public ParameterDefinition type(String type)
        {
            this.type = type;
            return this;
        }

        /**
         * Sets if the parameter is required or not.
         * If this attribute is not set, the parameter won't be required by default.
         * @param required True is the parameter is required.
         * @return
         */
        public ParameterDefinition required(boolean required)
        {
            this.required = required;
            return this;
        }

        /**
         * Sets the parameter's default value
         * @param defaultValue The default value
         * @return This instance
         */
        public ParameterDefinition defaultValue(String defaultValue)
        {
            this.defaultValue = defaultValue;
            return this;
        }

        /**
         * Returns a List representation of this ParameterDefinition, in the following form:
         * - 0 : Parameter name
         * - 1 : Parameter description. Useful for GUI rendition
         * - 2 : Parameter type. Useful for GUI rendition.
         * - 3 : Required or Optional parameter. Useful for GUI validation.
         * - 4 : Default value
         * @return Parameter definition as List
         */
        public List<String> getAsList()
        {
            List<String> list = new ArrayList<String>();
            list.add(name);
            list.add(label);
            list.add(type);
            list.add(Boolean.toString(required));
            list.add(defaultValue);
            return list;
        }
    }
}