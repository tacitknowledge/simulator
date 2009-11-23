package com.tacitknowledge.simulator.scripting;

import java.lang.reflect.Field;
import java.lang.reflect.Array;

import java.util.*;
import java.util.Map.Entry;

import com.tacitknowledge.simulator.SimulatorException;
import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtField;
import javassist.Modifier;
import javassist.NotFoundException;

import com.tacitknowledge.simulator.SimulatorPojo;
import org.apache.log4j.Logger;

/**
 * @author galo
 */
public class PojoClassGenerator
{
    /**
     * Base (and fake) default package for temporary simulator JavaBeans
     */
    private static final String TMP_CLASS_PACKAGE = "simulator.pojo.tmp.";

    /**
     * The full package name in which the generated-classes are contained.
     * Defaults to #TMP_CLASS_PACKAGE
     */
    private String generatedClassesPackage = TMP_CLASS_PACKAGE;

    /**
     * Thr Javassist ClassPool used to generate temporary classes
     */
    private ClassPool pool;

    /**
     * Generated classes
     */
    //private List<CtClass> generatedClasses;
    private Map<String, CtClass> generatedClasses;

    /**                                                 
     * Logger for this class.
     */
    private static Logger logger = Logger.getLogger(PojoClassGenerator.class);

    /**
     * Constructor
     *
     * @param pool The ClassPool to be used.
     */
    public PojoClassGenerator(ClassPool pool)
    {
        this.pool = pool;
        this.generatedClasses = new HashMap<String, CtClass>();
    }

    /**
     * Constructor                                                  
     *
     * @param pool        The ClassPool to be used.
     * @param packageName The full package name in which the generated-classes are contained.
     *                    String should end with a dot. e.g.: "simulator.tmp."
     */
    public PojoClassGenerator(ClassPool pool, String packageName)
    {
        this(pool);
        this.generatedClassesPackage = packageName;
    }

    /**
     * @return The full package name in which the generated-classes are contained
     */
    public String getGeneratedClassesPackage()
    {
        return this.generatedClassesPackage;
    }

    /**
     * Generates and returns a Map containing dinamically-generated
     * JavaBeans from the passed SimulatorPojo.
     * The generated classes and their populated instances will be passed
     * to the scripting engine as available data
     *
     * @param pojo The SimulatorPojo generated in the corresponding inbound Adapter
     * @return A Map containing dinamically-generated JavaBeans
     * @throws CannotCompileException If an error happens while trying to generate the
     *                                classes or their fields
     * @throws NotFoundException      If a referenced class cannot be found in the ClassPool
     * @throws ScriptException        If an error happens while trying to populate the
     *                                generated-class beans
     */
    public Map<String, Object> generateBeansMap(SimulatorPojo pojo)
            throws CannotCompileException, NotFoundException, ScriptException, SimulatorException
    {
        //Ê--- The SimulatorPojo's root must contain only one entry
        // --- Beans Map should only contain ONE entry
        if (pojo.getRoot().isEmpty() || pojo.getRoot().size() > 1)
        {
            throw new SimulatorException(
                    "beansMap should be neither empty nor contain more than 1 entry.");
        }

        // --- First, generate the temporary classes for the SimulatorPojo contents
        generateClassFromMap(null, pojo.getRoot());

        // --- Now populate the beans contained in the SimulatorPojo
        Map<String, Object> beansMap = new HashMap<String, Object>();
        for (Entry<String, Object> entry : pojo.getRoot().entrySet())
        {
            String className = getValidClassName(entry.getKey());
            Map<String, Object> bean = (Map<String, Object>) entry.getValue();

            beansMap.put(
                    entry.getKey(),
                    populateClassIntanceFromMap(getPackClassName(className), bean));
        }

        return beansMap;
    }

    /**
     * @return The list of generated class names.
     * It will be empty if called before generateBeansMap or after detachGeneratedClasses
     */
    public List<String> getGeneratedClassNames()
    {
        List<String> list = new ArrayList<String>();
        for (Entry<String, CtClass> entry : this.generatedClasses.entrySet())
        {
            list.add(entry.getValue().getName());
        }
        return list;
    }

    /**
     *
     * @param packClassName The package/class name
     * @return The real package/class name for the generated class, including time stamp or null
     * if that class alias hasn't been created
     */
    public String getRealGeneratedClassName(String packClassName)
    {
        if (generatedClasses.get(packClassName) != null)
        {
            return generatedClasses.get(packClassName).getName();
        }
        return null;
    }

    /**
     * Detaches the generated JavaBean classes to reduce memory consumption.
     * It will also remove the classes from the generatedClasses list
     */
    public void detachGeneratedClasses()
    {
        // ---
        for (Entry<String, CtClass> entry : this.generatedClasses.entrySet())
        {
            logger.debug("Detaching from pool generated class " + entry.getValue().getName());
            logger.debug("Detaching from pool generated class " + entry.getValue().getName());
            entry.getValue().detach();
        }
        this.generatedClasses.clear();
    }

    /**
     * Generates a new temporary class from the passed Map.
     * Map contents will be handled in the following way:
     * - Maps will be considered attributess of the subyacent generated class type
     * - Lists will be considered Array attributes of either the subyacent generated
     * class type or String array
     * depending on the List contents
     * - Strings will be considered String attributes
     *
     * @param className The name of the class to be generated
     * @param attr      A Map containing the attributes to be created for the new class
     * @return A new CtClass with its fields/attributes defined
     * @throws CannotCompileException If an error happens while trying to generate the
     *                                class or its fields
     * @throws NotFoundException      If a referenced class cannot be found in the ClassPool
     */
    private CtClass generateClassFromMap(String className, Map<String, Object> attr)
            throws CannotCompileException, NotFoundException
    {
        CtClass ctClass = null;
        boolean isNewClass = false;

        // --- We need to keep track of the "base" class name and map it
        // to the actual generated class name that will
        // include time to prevent collision issues
        String packClassName = null;

        if (className != null)
        {
            packClassName = getPackClassName(className);
            try
            {
                // --- First, try to get a class with the assembled name...
                ctClass = pool.get(packClassName);
            }
            catch (NotFoundException e)
            {
                // --- ...if it hasn't been created, make a new one
                // with the last package name being a time string
                logger.debug("Generating new temporary class " + getPackTimeClassName(className));
                ctClass = pool.makeClass(getPackTimeClassName(className));
                isNewClass = true;
            }
        }

        // --- If it's a new class
        if (className == null || isNewClass)
        {
            // --- Iterate through all the Map contents

            for (Entry<String, Object> entry : attr.entrySet())
            {
                String itemName = entry.getKey();
                Object itemValue = entry.getValue();

                // --- If another genereated-class is needed, we'll use the fully-qualified
                String fullName;
                if (className == null)
                {
                    fullName = getValidClassName(itemName);
                }
                else
                {
                    fullName = className + "." + getValidClassName(itemName);
                }

                // --- The new field/attribute to be added
                CtField newField;

                // --- Check the item type
                if (itemValue instanceof Map)
                {
                    // --- If the Value is a Map container, go down the structure
                    CtClass mapClass =
                            generateClassFromMap(fullName, (Map<String, Object>) itemValue);

                    // --- Generate the new field with the generated type
                    newField = generateField(mapClass.getName(), itemName, ctClass);
                }
                else if (itemValue instanceof List)
                {
                    // --- If the value is a List, verify the type of its contents
                    String fieldClassName;
                    Object val = ((List) itemValue).get(0);
                    if (val instanceof Map)
                    {
                        // --- If the first element in the list is a Map,
                        // generate a new class for it...
                        CtClass arrayClass =
                                generateClassFromMap(fullName, (Map<String, Object>) val);
                        // --- ...then generate an Array-representation field of this type
                        fieldClassName = "[L" + arrayClass.getName() + ";";
                    }
                    else
                    {
                        // --- Ff the element is a String, then the field is an Array of Strings
                        fieldClassName = new String[0].getClass().getName();
                    }
                    newField = generateField(fieldClassName, itemName, ctClass);
                }
                else
                {
                    // --- Anything else should be binded as it is
                    // (should be safe to assume it's a String)
                    newField = generateField(itemValue.getClass().getName(), itemName, ctClass);
                }

                if (ctClass != null && newField != null)
                {
                    ctClass.addField(newField);
                }
            }

            // --- If ctClass was generated and NOT frozen (meaning, not previously registered),
            // REGISTER it and add it the generatedClasses list
            if (ctClass != null)
            {
                ctClass.toClass();
                generatedClasses.put(packClassName, ctClass);
            }
        }

        return ctClass;
    }

    /**
     * Generates a new public field/attribute with the given type for a given CtClass
     *
     * @param className The class name of the field/attribute type
     * @param fieldName The field/attribute name
     * @param destClass The CtClass this field will be added to
     * @return A new CtField to be added to ctClass
     * @throws NotFoundException      If the className name is not found in the ClassPool
     * @throws CannotCompileException If the field cannot be created for some reason
     */
    private CtField generateField(String className, String fieldName, CtClass destClass)
            throws NotFoundException, CannotCompileException
    {
        if (destClass == null)
        {
            return null;
        }

        CtField ctField = new CtField(pool.get(className), fieldName, destClass);
        ctField.setModifiers(Modifier.PUBLIC);

        return ctField;
    }

    /**
     * Populates a generated class with the contents of a Map.
     * The class should have been generated from the same Map
     * to avoid conflicts or information loss
     *
     * @param packClassName The name of the dinamically-generated class to be populated
     * @param values    T   he values used to populate the bean
     * @return A dinamically-generated-class instance populated with the values data
     * @throws ScriptException If an error happens while trying to populate the bean
     */
    private Object populateClassIntanceFromMap(String packClassName, Map<String, Object> values)
            throws ScriptException
    {
        Object obj = null;
        if (packClassName != null)
        {
            // --- If there's a className ...
            String realName = generatedClasses.get(packClassName).getName();
            try
            {
                // --- Try to get an instance of the generated class
                obj = Class.forName(realName).newInstance();

                // --- Iterate through all the Map values

                for (Entry<String, Object> entry : values.entrySet())
                {
                    String itemName = entry.getKey();
                    Object itemValue = entry.getValue();

                    String fullName = packClassName + "." + getValidClassName(itemName);

                    // --- Get the actual Field from the itemName and field value from itemValue
                    Field field = obj.getClass().getDeclaredField(itemName);
                    Object fieldValue = itemValue;

                    // --- If the itemValue is...
                    if (itemValue instanceof Map)
                    {
                        // --- ...a Map, populate the corresponding generated-class bean
                        fieldValue = populateClassIntanceFromMap(fullName,
                                (Map<String, Object>) itemValue);
                    }
                    else if (itemValue instanceof List)
                    {
                        // --- ...a List, create an Array from the corresponding generated-class
                        // type and populate it
                        fieldValue = populateArrayFromList(fullName,
                                (List) itemValue);
                    }
                    // --- Assign the field value
                    field.set(obj, fieldValue);
                }
            }
            catch (ClassNotFoundException e)
            {
                String errorMessage = "Class : " + realName + " not found.";
                logger.error(errorMessage, e);
                throw new ScriptException(errorMessage, e);
            }
            catch (InstantiationException e)
            {
                String errorMessage = "Object for class : " + realName
                        + " couldn't be instantiated.";
                logger.error(errorMessage, e);
                throw new ScriptException(errorMessage, e);
            }
            catch (IllegalAccessException e)
            {
                String errorMessage =
                        "Object for class : " + realName + " couldn't be accessed.";
                logger.error(errorMessage, e);
                throw new ScriptException(errorMessage, e);
            }
            catch (SecurityException e)
            {
                String errorMessage =
                        "Unexpected security exception for class " + realName;
                logger.error(errorMessage, e);
                throw new ScriptException(errorMessage, e);
            }
            catch (NoSuchFieldException e)
            {
                String errorMessage = "A field wasn't found for class " + realName;
                logger.error(errorMessage, e);
                throw new ScriptException(errorMessage, e);
            }
        }

        return obj;
    }

    /**
     * Creates an Array of the corresponding generated-class type
     * and populates it with the populated beans
     *
     * @param packClassName The class name of the Array's content type
     * @param items     The list of items to populate the Array from
     * @return The populated Array of generated-class type beans
     * @throws ScriptException If an error happens while trying to populate the bean
     */
    private Object populateArrayFromList(String packClassName, List items) throws ScriptException
    {
        try
        {
            Object array = null;
            for (int i = 0; i < items.size(); i++)
            {
                Object item = items.get(i);

                // --- If this is the first element, initialize the Array
                // (will use items.size() for Array length)
                if (array == null)
                {
                    // --- If the item is...
                    if (item instanceof Map)
                    {
                        // --- ...a Map, get the generated-class from the className
                        // and initialize the Array
                        // --- Get the actual class name from the generatedClasses map
                        String realName = generatedClasses.get(packClassName).getName();
                        array = Array.newInstance(Class.forName(realName), items.size());
                    }
                    else
                    {
                        // --- ...anything else (should be safe to assume it's a String),
                        // initialize a Strings Array
                        array = Array.newInstance(String.class, items.size());
                    }
                }

                // --- If the item is...
                if (item instanceof Map)
                {
                    // --- ...a Map, it's a CtClass instance, so populate it and then assign
                    Array.set(
                            array,
                            i,
                            populateClassIntanceFromMap(packClassName, (Map<String, Object>) item));
                }
                else
                {
                    // --- Otherwise, assign as it is (should be safe to assume it's a String)
                    Array.set(array, i, item);
                }
            }
            return array;
        }
        catch (ClassNotFoundException e)
        {
            throw new ScriptException("Unexpected exception trying to instantiate temp class "
                    + generatedClasses.get(packClassName).getName(), e);
        }
    }

    /**
     * Returns a valid Java class name (capitalized, no non-word characters)
     *
     * @param name The original class name
     * @return The valid Java class name
     */
    private String getValidClassName(String name)
    {
        // --- First, strip away non-word characters
        name = name.replaceAll("\\W", "");
        // --- Capitalize
        return (name.substring(0, 1).toUpperCase() + name.substring(1).toLowerCase());
    }

    private String getPackClassName(String name)
    {                                                                           
        return generatedClassesPackage + name;
    }

    private String getPackTimeClassName(String name)
    {
        return generatedClassesPackage + new Date().getTime() + "." + name;
    }
}
