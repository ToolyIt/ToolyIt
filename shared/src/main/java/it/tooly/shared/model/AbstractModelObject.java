package it.tooly.shared.model;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import it.tooly.shared.common.ToolyException;
import it.tooly.shared.model.attribute.AttrType;
import it.tooly.shared.model.attribute.IModelObjectAttribute;
import it.tooly.shared.model.attribute.ModelObjectAttribute;

/**
 * An object with an id and a name. Subclasses can safely implement
 * {@link IModelObject}
 */
public abstract class AbstractModelObject implements IModelObjectListenable {
	protected String id;
	protected Map<String, IModelObjectAttribute<?>> attributes;
	protected Map<IModelObjectAttribute<?>, Object> attributeValues;
	private final PropertyChangeSupport propertyChangeSupport;

	public AbstractModelObject(String id) {
		this(id, "Object[" + id + "]", null);
	}

	public AbstractModelObject(String id, String name) {
		this(id, name, null);
	}

	/**
	 * @param id
	 * @param name
	 * @param strAttrs
	 */
	public AbstractModelObject(String id, String name, Set<String> strAttrs) {
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

		if (strAttrs != null && strAttrs.size() > 0) {
			for (String attrName : strAttrs) {
				addStringAttribute(attrName, "");
			}
		}

		/*
		 * Create property change support so this object can be used as the
		 * source for events
		 */
		propertyChangeSupport = new PropertyChangeSupport(this);
	}

	/**
	 * Add a certain type of attribute (with some matching type of value) to
	 * this object.
	 *
	 * @param <T>
	 *
	 * @param attribute
	 *            - A ModelObjectAttribute
	 * @param aValue
	 *            - The attribute value (may be null)
	 */
	protected <T> void addAttribute(IModelObjectAttribute<T> attribute, T aValue) {
		this.attributes.put(attribute.getName(), attribute);
		this.attributeValues.put(attribute, aValue);
	}

	/**
	 * Add a certain type of attribute (with some matching type of value) to
	 * this object.
	 *
	 * @param <T>
	 *
	 * @param aName
	 *            - Name of the attribute.
	 * @param aType
	 *            - The type of the attribute.
	 * @param aValue
	 *            - The value of the attribute (may be null).
	 * @throws ToolyException
	 */
	protected <T> void addAttribute(String aName, AttrType aType, T aValue) {
		IModelObjectAttribute<T> attribute = new ModelObjectAttribute<>(aName, aType);
		addAttribute(attribute, aValue);
	}

	/**
	 * Add a certain type of attribute (with some matching type of value) to
	 * this object.
	 *
	 * @param <T>
	 *
	 * @param aName
	 *            - Name of the attribute.
	 * @param aValue
	 *            - The (String) value of the attribute.
	 * @throws ToolyException
	 */
	protected void addStringAttribute(String aName, String aValue) {
		IModelObjectAttribute<String> attribute = ModelObjectAttribute.createStringAttr(aName);
		addAttribute(attribute, aValue);
	}

	public Collection<IModelObjectAttribute<?>> getAttrs() {
		return this.attributes.values();
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

	public IModelObjectAttribute<?> getAttr(String attrName) {
		return this.attributes.get(attrName);
	}

	public AttrType getAttrType(String attrName) {
		IModelObjectAttribute<?> objAttr = this.attributes.get(attrName);
		return objAttr == null ? null : objAttr.getType();
	}

	@SuppressWarnings("unchecked")
	public <T> T getAttrValue(IModelObjectAttribute<T> attribute) {
		Object attrValue = this.attributeValues.get(attribute);
		return (T) attrValue;
	}

	public Object getAttrValue(String attributeName) {
		IModelObjectAttribute<?> attribute = getAttr(attributeName);
		if (attribute == null)
			return null;
		return getAttrValue(attribute);
	}

	public Object getAttrValueAt(int index) {
		String attrName = getAttrName(index);
		return attrName == null ? null : this.getAttrValue(attrName);
	}

	public String getAttrValueAsString(IModelObjectAttribute<?> attribute) {
		Object attrValue = this.attributeValues.get(attribute);
		return getAttrValueAsString(attribute.getType(), attrValue);
	}

	public static String getAttrValueAsString(AttrType attrType, Object attrValue) {
		String textFormat = attrType.getTextFormat();
		return String.format(textFormat, attrValue);
	}

	@SuppressWarnings("unchecked")
	public <T> boolean setAttrValue(String attributeName, T attrValue) {
		if (!hasAttr(attributeName))
			return false;
		IModelObjectAttribute<?> attribute = getAttr(attributeName);
		if (attribute.getValueClass().isAssignableFrom(attrValue.getClass())) {
			return this.setAttrValue((IModelObjectAttribute<T>) attribute, attrValue);
		} else {
			return false;
		}
	}

	public <T> boolean setAttrValue(IModelObjectAttribute<T> attribute, T attrValue) {
		AttrType aType = attribute.getType();
		if (aType.getValueClass().isAssignableFrom(attrValue.getClass())) {
			Object oldValue = this.attributeValues.put(attribute, attrValue);
			this.propertyChangeSupport.firePropertyChange(attribute.getName(), oldValue, attrValue);
			return true;
		} else {
			return false;
		}
	}

	public boolean hasAttr(String attrName) {
		return this.attributes.containsKey(attrName);
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
		IModelObjectAttribute<?> attribute = getAttr("name");
		return attribute == null ? null : this.getAttrValueAsString(attribute);
	}

	/**
	 * @param name
	 *            the name to set
	 */
	public void setName(String name) {
		setAttrValue("name", name);
	}

	public void addPropertyChangeListener(PropertyChangeListener listener) {
		this.propertyChangeSupport.addPropertyChangeListener(listener);
	}

	public void addPropertyChangeListener(IModelObjectAttribute<?> attribute, PropertyChangeListener listener) {
		this.propertyChangeSupport.addPropertyChangeListener(attribute.getName(), listener);
	}

	public void removePropertyChangeListener(PropertyChangeListener listener) {
		this.propertyChangeSupport.removePropertyChangeListener(listener);
	}

	public void removePropertyChangeListener(IModelObjectAttribute<?> attribute, PropertyChangeListener listener) {
		this.propertyChangeSupport.removePropertyChangeListener(attribute.getName(), listener);
	}

	public int compareTo(IModelObject o) {
		return this.getId().compareTo(o.getId());
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return this.getName();
	}

}
