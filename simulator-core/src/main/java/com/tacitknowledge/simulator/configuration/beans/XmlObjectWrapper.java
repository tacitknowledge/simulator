package com.tacitknowledge.simulator.configuration.beans;

/**
 * A wrapper for an actual data object with valuable information about object representation
 * in the SOAP message, such as element namespace.
 * 
 * @author Andrian Rusnac (arusnac@tacitknowledge.com)
 */
public class XmlObjectWrapper
{
	/** XML namespace of the wrapped object */
	private String namespace;
	/** XML namespace prefix of the wrapped object */
	private String prefix;
	/** The actual object that is part of the SOAP message */
	private Object value;
	
	/** Default constructor */
	public XmlObjectWrapper()
	{
	}

	/**
	 * Constructor that populates field values.
	 * @param namespace XML namespace of the wrapped object
	 * @param value The actual object that is part of the SOAP message
	 */
	public XmlObjectWrapper(String namespace, Object value)
	{
		this(namespace, null, value);
	}

	/**
	 * Constructor that populates field values.
	 * @param namespace XML namespace of the wrapped object
	 * @param prefix XML namespace prefix of the wrapped object
	 * @param value The actual object that is part of the SOAP message
	 */
	public XmlObjectWrapper(String namespace, String prefix, Object value)
	{
		this.namespace = namespace;
		this.prefix = prefix;
		this.value = value;
	}

	/**
	 * @return the namespace
	 */
	public String getNamespace()
	{
		return namespace;
	}
	/**
	 * @param namespace the namespace to set
	 */
	public void setNamespace(String namespace)
	{
		this.namespace = namespace;
	}
	/**
	 * @return the value
	 */
	public Object getValue()
	{
		return value;
	}
	/**
	 * @param value the value to set
	 */
	public void setValue(Object value)
	{
		this.value = value;
	}

	/**
	 * @return the prefix
	 */
	public String getPrefix() {
		return prefix;
	}

	/**
	 * @param prefix the prefix to set
	 */
	public void setPrefix(String prefix) {
		this.prefix = prefix;
	}
}
