package it.tooly.shared.model;

import java.util.Collection;
import java.util.Set;

import it.tooly.shared.model.attribute.AttrType;
import it.tooly.shared.model.attribute.IModelObjectAttribute;
import it.tooly.shared.model.attribute.ModelObjectAttribute;

/**
 * An interface for a model object. Each object should have an id (with get
 * method) and a name (with get and set method).
 *
 * @author M.E. de Boer
 *
 */
public interface IModelObject extends Comparable<IModelObject>  {
	/**
	 * Default id for the object if none has been specified
	 */
	public static final String NULL_ID = "OBJECT_WITHOUT_ID";

	public Collection<IModelObjectAttribute<?>> getAttrs();

	public Set<String> getAttrNames();

	/**
	 * Get an attribute with a given name
	 *
	 * @param attrName
	 *            Name of the attribute
	 * @return A {@link StandardModelObjectAttribute}, if an attribute with the
	 *         given name exists for this object
	 */
	public IModelObjectAttribute<?> getAttr(String attrName);

	/**
	 * Get the type of the attribute with a given name. This is convenience
	 * method. The implementing class could call {@link #getAttr(String)} and
	 * then {@link ModelObjectAttribute#getType()} if the attribute exists.
	 *
	 * @param attrName
	 *            Name of the attribute
	 * @return A {@link IAttrType}
	 */
	public AttrType getAttrType(String attrName);

	/**
	 * Get the <b>value</b> of the attribute with a given name.
	 *
	 * @param attrName
	 *            Name of the attribute
	 * @return The attribute value, which extends {@link Object}
	 */
	public Object getAttrValue(String attrName);

	public <T> T getAttrValue(IModelObjectAttribute<T> attribute);

	public <T> boolean setAttrValue(String attrName, T attrValue);

	public boolean hasAttr(String attrName);

	public String getId();

	public boolean hasNullId();

	public String getName();

	public void setName(String name);


}
