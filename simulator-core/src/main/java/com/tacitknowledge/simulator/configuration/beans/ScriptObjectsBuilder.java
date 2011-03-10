package com.tacitknowledge.simulator.configuration.beans;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A helper class that can be used in the scenario editor to create useful java classes.
 * 
 * @author Andrian Rusnac (arusnac@tacitknowledge.com)
 */
public class ScriptObjectsBuilder
{
	/**
	 * When invoked by the scripting engine will return an empty map.
	 * @return an empty map
	 */
	@SuppressWarnings("rawtypes")
	public Map newMap()
	{
		return new HashMap();
	}
	
	/**
	 * When invoked by the scripting engine will return an empty list.
	 * @return an empty list
	 */
	@SuppressWarnings("rawtypes")
	public List newList()
	{
		return new ArrayList();
	}

	/**
	 * When invoked by the scripting engine will return an empty XmlObjectWrapper.
	 * @return an empty XmlObjectWrapper
	 */
	public XmlObjectWrapper newWrapper()
	{
		return new XmlObjectWrapper();
	}
	
	/**
	 * When invoked by the scripting engine will return a populated XmlObjectWrapper.
	 * @param namespace XML namespace of the wrapped object
	 * @param value The actual object that is part of the SOAP message
	 * @return a populated XmlObjectWrapper.
	 */
	public XmlObjectWrapper newWrapper(String namespace, Object value)
	{
		return new XmlObjectWrapper(namespace, value);
	}

	/**
	 * When invoked by the scripting engine will return a populated XmlObjectWrapper.
	 * @param namespace XML namespace of the wrapped object
	 * @param prefix XML namespace prefix of the wrapped object
	 * @param value The actual object that is part of the SOAP message
	 * @return a populated XmlObjectWrapper.
	 */
	public XmlObjectWrapper newWrapper(String namespace, String prefix, Object value)
	{
		return new XmlObjectWrapper(namespace, prefix, value);
	}
}
