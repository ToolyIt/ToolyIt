/*******************************************************************************
 * Copyright (c) 2005, 2015 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package it.tooly.shared.model;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

/**
 * An object with an id and a name. Subclasses can safely implement
 * {@link IModelObject}
 */
public abstract class AbstractModelObject {
	protected String id;
	protected Map<String, Class<? extends Object>> attributes;
	protected Map<String, Object> attributeValues;

	public AbstractModelObject(String id) {
		this.init(id, "");
	}

	public AbstractModelObject(String id, String name) {
		this.init(id, name);
	}

	private void init(String id, String name) {
		this.id = id;
		/*
		 * Create attributes and attributeValues maps. They are both
		 * LinkedHashMap because they should be in the same order.
		 */
		this.attributes = new LinkedHashMap<>();
		this.attributeValues = new LinkedHashMap<>();
		/*
		 * By default this object has a name attribute
		 */
		addStringAttribute("name", name);
	}

	/**
	 * Add a certain type of attribute (with some matching type of value) to
	 * this object.
	 *
	 * @param aName
	 *            - Name of the attribute.
	 * @param aClass
	 *            - A Class (T) that specifies the value type of the attribute.
	 * @param aValue
	 *            - The value of the attribute. Should be of the correct type
	 *            (T).
	 */
	protected <T extends Object> void addAttribute(String aName, Class<T> aClass, T aValue) {
		this.attributes.put(aName, aClass);
		this.attributeValues.put(aName, aValue);
	}

	/**
	 * Add a certain type of attribute (with some matching type of value) to
	 * this object.
	 *
	 * @param aName
	 *            - Name of the attribute.
	 * @param aValue
	 *            - The (String) value of the attribute.
	 */
	protected <T extends Object> void addStringAttribute(String aName, String aValue) {
		this.attributes.put(aName, String.class);
		this.attributeValues.put(aName, aValue);
	}

	public Set<String> getAttrNames() {
		return this.attributes.keySet();
	}

	public Class<? extends Object> getAttrClass(String attrName) {
		return this.attributes.get(attrName);
	}

	public Object getAttrValue(String attrName) {
		return this.attributeValues.get(attrName);
	}

	public void setAttrValue(String attrName, Object attrValue) {
		if (hasAttr(attrName))
			this.attributeValues.put(attrName, attrValue);
	}

	public boolean hasAttr(String attrName) {
		return this.attributes.containsKey(attrName);
	}

	/**
	 * @return {@code false}, as this is not an observable object
	 */
	public boolean isObservable() {
		return false;
	}

	public String getId() {
		return this.id;
	}

	protected void setId(String newId) {
		this.id = newId;
	}

	public boolean hasNullId() {
		return IModelObject.NULL_ID.equals(this.id);
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return (String) this.attributeValues.get("name");
	}

	/**
	 * @param name
	 *            the name to set
	 */
	public void setName(String name) {
		this.attributeValues.put("name", name);
	}

	public int compareTo(IModelObject o) {
		return this.getId().compareTo(o.getId());
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return this.getName() + " (" + id + ")";
	}

}
