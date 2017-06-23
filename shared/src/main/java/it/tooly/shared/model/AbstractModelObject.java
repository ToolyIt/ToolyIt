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
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import it.tooly.shared.common.ToolyException;
import it.tooly.shared.model.ModelObjectAttribute.AttrType;

/**
 * An object with an id and a name. Subclasses can safely implement
 * {@link IModelObject}
 */
public abstract class AbstractModelObject implements IModelObject {
	protected String id;
	protected Map<String, ModelObjectAttribute<? extends Object>> attributes;
	// protected Map<String, Object> attributeValues;

	public AbstractModelObject(String id) {
		this.init(id, "");
	}

	public AbstractModelObject(String id, String name) {
		this.init(id, name);
	}

	public AbstractModelObject(String id, String name, List<String> strAttrs) {
		this.init(id, name);
		if (strAttrs != null && strAttrs.size() > 0) {
			for (String attrName : strAttrs) {
				addStringAttribute(attrName, "");
			}
		}
	}

	private void init(String id, String name) {
		this.id = id;
		/*
		 * Create attributes and attributeValues maps. They are both
		 * LinkedHashMap because they should be in the same order.
		 */
		this.attributes = new LinkedHashMap<>();
		// this.attributeValues = new LinkedHashMap<>();
		/*
		 * By default this object has a name attribute
		 */
		addStringAttribute("name", name);
	}

	/**
	 * Add a certain type of attribute (with some matching type of value) to
	 * this object.
	 *
	 * @param attribute
	 *            - A ModelObjectAttribute
	 */
	protected void addAttribute(ModelObjectAttribute<?> attribute) {
		this.attributes.put(attribute.getName(), attribute);
	}

	/**
	 * Add a certain type of attribute (with some matching type of value) to
	 * this object.
	 *
	 * @param aName
	 *            - Name of the attribute.
	 * @param aType
	 *            - The type of the attribute.
	 * @param aValue
	 *            - The value of the attribute. Should be of the correct type
	 *            (T).
	 * @throws ToolyException
	 */
	protected void addAttribute(String aName, AttrType aType, Object aValue) throws ToolyException {
		this.attributes.put(aName, new ModelObjectAttribute<>(aName, aValue, aType));
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
		try {
			this.attributes.put(aName, new ModelObjectAttribute<>(aName, aValue, String.class));
		} catch (ToolyException e) {
			e.printStackTrace();
		}
		// this.attributeValues.put(aName, aValue);
	}

	public Set<ModelObjectAttribute<?>> getAttrs() {
		return new LinkedHashSet<>(this.attributes.values());
	}

	public Set<String> getAttrNames() {
		return this.attributes.keySet();
	}

	public String getAttrName(int index) {
		Set<String> attrNames = getAttrNames();
		if (attrNames != null && !attrNames.isEmpty() && attrNames.size() > index) {
			String[] attrNamesArray = new String[attrNames.size()];
			attrNames.toArray(attrNamesArray);
			return attrNamesArray[index];
		} else {
			return null;
		}
	}

	public AttrType getAttrType(String attrName) {
		ModelObjectAttribute<?> objAttr = this.attributes.get(attrName);
		return objAttr == null ? null : objAttr.getType();
	}

	public Object getAttrValue(String attrName) {
		return this.attributes.get(attrName).getValue();
	}

	public Object getAttrValueAt(int index) {
		String attrName = getAttrName(index);
		return attrName == null ? null : this.getAttrValue(attrName);
	}

	public void setAttrValue(String attrName, Object attrValue) {
		if (hasAttr(attrName)) {
			this.attributes.get(attrName).setValue(attrValue);
		}
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
		return this.attributes.get("name").getValue().toString();
	}

	/**
	 * @param name
	 *            the name to set
	 */
	public void setName(String name) {
		ModelObjectAttribute<? extends Object> nameAttr = this.attributes.get("name");
		if (nameAttr != null) {
			nameAttr.setValue(name);
		}
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
