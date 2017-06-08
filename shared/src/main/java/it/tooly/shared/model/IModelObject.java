package it.tooly.shared.model;

import java.util.Set;

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

	/**
	 * @return true if this is an observable object. In this case, the object
	 *         should also implement some kind of Observable interface.
	 */
	public boolean isObservable();

	public Set<String> getAttrNames();

	public Class<? extends Object> getAttrClass(String attrName);

	public Object getAttrValue(String attrName);

	public void setAttrValue(String attrName, Object attrValue);

	public boolean hasAttr(String attrName);

	public String getId();

	public boolean hasNullId();

	public String getName();

	public void setName(String name);
}
