package com.tacitknowledge.simulator.scripting;

import javassist.*;

import java.util.*;
import java.lang.reflect.Field;
import java.lang.reflect.Array;

import com.tacitknowledge.simulator.SimulatorPojo;
import org.apache.log4j.Logger;

/**
 * @author galo
 */
public class PojoClassGenerator
{
    /**
     * Base (and fake) package for temporary simulator JavaBeans
     */
    private final static String TMP_CLASS_PACKAGE = "simulator.pojo.tmp.";

    /**
     * Thr Javassist ClassPool used to generate temporary classes
     */
    private ClassPool pool;

    /**
     * Generated classes 
     */
    private List<CtClass> generatedClasses;

    /**
     * Logger for this class.
     */
    private static Logger logger = Logger.getLogger(PojoClassGenerator.class);

    /**
     * Constructor
     * @param pool The ClassPool to be used.
     */
    public PojoClassGenerator(ClassPool pool) {
        this.pool = pool;
        this.generatedClasses = new ArrayList<CtClass>();
    }

    /**
     * Generates and returns a Map containing dinamically-generated JavaBeans from the passed SimulatorPojo.
     * The generated classes and their populated instances will be passed to the scripting engine as available data
     * @param pojo The SimulatorPojo generated in the corresponding inbound Adapter
     * @return A Map containing dinamically-generated JavaBeans
     * @throws CannotCompileException If an error happens while trying to generate the classes or their fields
     * @throws NotFoundException If a referenced class cannot be found in the ClassPool
     * @throws ScriptException If an error happens while trying to populate the generated-class beans
     */
    public Map generateBeansMap(SimulatorPojo pojo)
            throws CannotCompileException, NotFoundException, ScriptException
    {
        // --- First, generate the temporary classes for the SimulatorPojo contents
        generateClassFromMap(null, pojo.getRoot());

        // --- Now populate the beans contained in the SimulatorPojo
        Map<String, Object> beansMap = new HashMap<String,Object>();
        Iterator i = pojo.getRoot().keySet().iterator();
        while (i.hasNext())
        {
            String beanName = (String) i.next();
            Map bean = (Map) pojo.getRoot().get(beanName);

            beansMap.put(beanName, populateClassIntanceFromMap(TMP_CLASS_PACKAGE + beanName, bean));
        }

        return beansMap;
    }

    /**
     * Detaches the generated JavaBean classes to reduce memory consumption
     */
    public void detachGeneratedClasses() {
        for (CtClass klass : this.generatedClasses) {
            klass.detach();
        }
    }

    /**
     * Generates a new temporary class from the passed Map.
     * Map contents will be handled in the following way:
     *  - Maps will be considered attributess of the subyacent generated class type
     *  - Lists will be considered Array attributes of either the subyacent generated class type or String array
     *      depending on the List contents
     *  - Strings will be considered String attributes
     * @param className The name of the class to be generated
     * @param attr A Map containing the attributes to be created for the new class
     * @return A new CtClass with its fields/attributes defined
     * @throws CannotCompileException If an error happens while trying to generate the class or its fields
     * @throws NotFoundException If a referenced class cannot be found in the ClassPool
     */
    private CtClass generateClassFromMap(String className, Map attr)
            throws CannotCompileException, NotFoundException
    {
        CtClass ctClass = null;
        if (className != null)
        {
            try
            {
                // --- First, try to get a class with the assembled name...
                ctClass = pool.get(TMP_CLASS_PACKAGE + className);
            }
            catch (NotFoundException e)
            {
                // --- ...if it hasn't been created, make a new one
                logger.debug("Generating new temporary class " + TMP_CLASS_PACKAGE + className);
                ctClass = pool.makeClass(TMP_CLASS_PACKAGE + className);
            }
        }

        // --- Iterate throu all the Map contents
        Iterator i = attr.keySet().iterator();
        while (i.hasNext())
        {
            String itemName = (String) i.next();
            Object itemValue = attr.get(itemName);

            // --- If another genereated-class is needed, we'll use the fully-qualified name (parent name + item name)
            String fullName = className == null ? itemName : className + "." + itemName;

            // --- The new field/attribute to be added
            CtField newField = null;

            // --- Check the item type
            if (itemValue instanceof Map)
            {
                // --- If the Value is a Map container, go down the structure
                CtClass mapClass = generateClassFromMap(fullName, (Map) itemValue);

                // --- Generate the new field with the generated type
                newField = generateField(mapClass.getName(), itemName, ctClass);
            }
            else if (itemValue instanceof List)
            {
                // --- If the value is a List, verify the type of its contents
                Object val = ((List) itemValue).get(0);
                if (val instanceof Map)
                {
                    // --- If the first element in the list is a Map, generate a new class for it...
                    CtClass arrayClass =
                            generateClassFromMap(fullName, (Map) val);
                    // --- ...then generate an Array-representation field of this type
                    newField = generateField("[L" + arrayClass.getName() + ";", itemName, ctClass);
                }
                else
                {
                    // --- Ff the element is a String, then the field is an Array of Strings
                    newField = generateField(new String[0].getClass().getName(), itemName, ctClass);
                }
            }
            else
            {
                // --- Anything else should be binded as it is (should be safe to assume it's a String)
                newField = generateField(itemValue.getClass().getName(), itemName, ctClass);
            }

            if (ctClass != null && newField != null) {
                ctClass.addField(newField);
            }
        }

        // --- If ctClass was generated, REGISTER it and add it the generatedClasses list
        if (ctClass != null)
        {
            ctClass.toClass();
            generatedClasses.add(ctClass);
        }
        
        return ctClass;
    }

    /**
     * Generates a new public field/attribute with the given type for a given CtClass
     * @param className The class name of the field/attribute type
     * @param fieldName The field/attribute name
     * @param destClass The CtClass this field will be added to
     * @return A new CtField to be added to ctClass
     * @throws NotFoundException If the className name is not found in the ClassPool
     * @throws CannotCompileException If the field cannot be created for some reason
     */
    private CtField generateField(String className, String fieldName, CtClass destClass)
            throws NotFoundException, CannotCompileException
    {
        if (destClass == null) {
            return null;
        }
        
        CtField ctField = new CtField(pool.get(className), fieldName, destClass);
        ctField.setModifiers(Modifier.PUBLIC);

        return ctField;
    }

    /**
     * Populates a generated class with the contents of a Map.
     * The class should have been generated from the same Map to avoid conflicts or information loss
     * @param className The name of the dinamically-generated class to be populated
     * @param values The values used to populate the bean
     * @return A dinamically-generated-class instance populated with the values data
     * @throws ScriptException If an error happens while trying to populate the generated-class bean
     */
    private Object populateClassIntanceFromMap(String className, Map values)
            throws ScriptException
    {
        Object obj = null;
        if (className != null) {
            // --- If there's a className ...
            try {
                // --- Try to get an instance of the generated class
                obj = Class.forName(className).newInstance();

                // --- Iterate throu the Map values
                Iterator i = values.keySet().iterator();
                while (i.hasNext()) {
                    String itemName = (String) i.next();
                    Object itemValue = values.get(itemName);

                    // --- Get the actual Field from the itemName and field value from the itemValue
                    Field field = obj.getClass().getDeclaredField(itemName);
                    Object fieldValue = itemValue;

                    // --- If the itemValue is...
                    if (itemValue instanceof Map) {
                        // --- ...a Map, populate the corresponding generated-class bean
                        fieldValue = populateClassIntanceFromMap(className + "." + itemName, (Map) itemValue);
                    } else if (itemValue instanceof List) {
                        // --- ...a List, create an Array from the corresponding generated-class type and populate it
                        fieldValue = populateArrayFromList(className + "." + itemName, (List) itemValue);
                    }
                    // --- Assign the field value
                    field.set(obj, fieldValue);
                }
            } catch(Exception e) {
                throw new ScriptException("Unexpected exception trying to instantiate temp class " + className + ": " + e.getMessage(), e);
            }
        }

        return obj;
    }

    /**
     * Creates an Array of the corresponding generated-class type and populates it with the populated beans
     * @param className The class name of the Array's content type
     * @param items The list of items to populate the Array from
     * @return The populated Array of generated-class type beans
     * @throws ScriptException If an error happens while trying to populate the generated-class bean
     */
    private Object populateArrayFromList(String className, List items) throws ScriptException {
        try {
            Object array = null;
            for (int i = 0; i < items.size(); i++) {
                Object item = items.get(i);

                // --- If this is the first element, initialize the Array (will use items.size() for Array length)
                if (array == null) {
                    // --- If the item is...
                    if (item instanceof Map) {
                        // --- ...a Map, get the generated-class from the className and initialize the Array
                        array = Array.newInstance(Class.forName(className), items.size());
                    } else {
                        // --- ...anything else (should be safe to assume it's a String), initialize a Strings Array
                        array = Array.newInstance(String.class, items.size());
                    }
                }

                // --- If the item is...
                if (item instanceof Map) {
                    // --- ...a Map, it's a CtClass instance, so populate it and then assign
                    Array.set(array, i, populateClassIntanceFromMap(className, (Map) item));
                } else {
                    // --- Otherwise, assign as it is (should be safe to assume it's a String)
                    Array.set(array, i, item);
                }
            }

            return  array;
        } catch(ClassNotFoundException e) {
                throw new ScriptException("Unexpected exception trying to instantiate temp class " + className, e);
        }
    }
}
