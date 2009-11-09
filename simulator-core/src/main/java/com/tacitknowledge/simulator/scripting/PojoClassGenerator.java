package com.tacitknowledge.simulator.scripting;

import javassist.*;

import java.util.Map;
import java.util.Iterator;
import java.util.List;
import java.util.HashMap;
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
     * Logger for this class.
     */
    private static Logger logger = Logger.getLogger(PojoClassGenerator.class);

    public PojoClassGenerator(ClassPool pool) {
        this.pool = pool;
    }

    public Map generateBeansMap(SimulatorPojo pojo)
            throws CannotCompileException, NotFoundException, ScriptException
    {
        // --- First, generate the temporary classes for the SimulatorPojo contents
        generateClassFromMap(null, pojo.getRoot());

        // --- Now populate the beans contained in the SimulatorPojo
        Map beansMap = new HashMap();
        Iterator i = pojo.getRoot().keySet().iterator();
        while (i.hasNext()) {
            String beanName = (String) i.next();
            Map bean = (Map) pojo.getRoot().get(beanName);

            beansMap.put(beanName, populateClassIntanceFromMap(TMP_CLASS_PACKAGE + beanName, bean));
        }

        return beansMap;
    }

    private CtClass generateClassFromMap(String className, Map attr)
            throws CannotCompileException, NotFoundException
    {
        CtClass ctClass = null;
        if (className != null)
        {
            try
            {
                ctClass = pool.get(TMP_CLASS_PACKAGE + className);
            }
            catch (NotFoundException e)
            {
                logger.debug("Generating new temporary class " + TMP_CLASS_PACKAGE + className);
                ctClass = pool.makeClass(TMP_CLASS_PACKAGE + className);
            }
        }

        Iterator i = attr.keySet().iterator();
        while (i.hasNext())
        {
            String itemName = (String) i.next();
            Object itemValue = attr.get(itemName);

            //String fullName = parentName == null ? itemName : parentName + "." + itemName;

            if (itemValue instanceof Map)
            {
                // --- If the Value is a container, go down the structure
                CtClass mapClass = generateClassFromMap((className == null ? itemName : className + "." + itemName), (Map) itemValue);

                if (ctClass != null)
                {
                    ctClass.addField(generateField(mapClass.getName(), itemName, ctClass));
                }
            }
            else if (itemValue instanceof List)
            {
                // --- If the value is a List, pass it down to the ListItems handler
                //bindListItems(manager, fullName, (List) itemValue);

                // --- If the first element is a Map, generate a new ctClass
                CtField newField = null;
                Object val = ((List) itemValue).get(0);
                if (val instanceof Map)
                {
                    CtClass arrayClass =
                            generateClassFromMap((className == null ? itemName : className + "." + itemName), (Map) val);
                    //System.out.println(" tmpClass as Array class name: [L" + arrayClass.getName() + ";");
                    newField = generateField("[L" + arrayClass.getName() + ";", itemName, ctClass);
                }
                else
                {
                    // --- if the element is a String, then this is an ArrayAttribute of the current ctClass
                    //System.out.println(" String[] class name: " + new String[0].getClass().getName());
                    newField = generateField(new String[0].getClass().getName(), itemName, ctClass);
                }
                // --- Add the array field

                ctClass.addField(newField);
            }
            else
            {
                // --- Anything else should be binded as it is
                //bindItem(manager, fullName, itemValue);
                ctClass.addField(generateField(itemValue.getClass().getName(), itemName, ctClass));
            }
        }

        // --- If ctClass was generated, REGISTER it
        if (ctClass != null)
            ctClass.toClass();
        
        return ctClass;
    }

    private CtField generateField(String className, String fieldName, CtClass destClass)
            throws NotFoundException, CannotCompileException
    {
        CtField ctField = new CtField(pool.get(className), fieldName, destClass);
        ctField.setModifiers(Modifier.PUBLIC);

        return ctField;
    }

    private Object populateClassIntanceFromMap(String className, Map values)
            throws ScriptException
    {
        Object obj = null;
        if (className != null) {
            // --- If there's a className ...
            try {
                // --- Try to get an instance of the generated class
                obj = Class.forName(className).newInstance();

                Iterator i = values.keySet().iterator();
                while (i.hasNext()) {
                    String itemName = (String) i.next();
                    Object itemValue = values.get(itemName);

                    // --- Get the actual Field and field value from the itemValue
                    Field field = obj.getClass().getDeclaredField(itemName);
                    Object fieldValue = itemValue;

                    if (itemValue instanceof Map) {
                        fieldValue = populateClassIntanceFromMap(className + "." + itemName, (Map) itemValue);
                    } else if (itemValue instanceof List) {
                        fieldValue = populateArrayFromList(className + "." + itemName, (List) itemValue);
                    }

                    field.set(obj, fieldValue);
                }
            } catch(Exception e) {
                throw new ScriptException("Unexpected exception trying to instantiate temp class " + className + ": " + e.getMessage(), e);
            }
        }

        return obj;
    }

    private Object populateArrayFromList(String className, List items) throws ScriptException {
        try {

            Object array = null;
            for (int i = 0; i < items.size(); i++) {
                Object item = items.get(i);

                if (array == null) {
                    if (item instanceof Map) {
                        array = Array.newInstance(Class.forName(className), items.size());
                    } else {
                        array = Array.newInstance(String.class, items.size());
                    }
                }

                if (item instanceof Map) {
                    // --- If the item is a Map, it's a CtClass instance, so populate it and then assing
                    Array.set(array, i, populateClassIntanceFromMap(className, (Map) item));
                } else {
                    // --- Otherwise, assign as it is
                    Array.set(array, i, item);
                }
            }

            return  array;
        } catch(ClassNotFoundException e) {
                throw new ScriptException("Unexpected exception trying to instantiate temp class " + className, e);
        }
    }
}
