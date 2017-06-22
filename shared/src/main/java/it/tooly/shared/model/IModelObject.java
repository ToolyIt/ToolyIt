package it.tooly.shared.model;

import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.lang.StringUtils;

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

	public static enum AttrType {
		STRING, INTEGER, DOUBLE, BOOLEAN, DATE;
		public Class<? extends Object> getTypeClass() {
			String className = StringUtils.capitalize(this.toString().toLowerCase());
			try {
				return Class.forName("java.lang." + className);
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
				return null;
			}
		}
	}

	/**
	 * @return true if this is an observable object. In this case, the object
	 *         should also implement some kind of Observable interface.
	 */
	public boolean isObservable();

	public Set<Entry<String, AttrType>> getAttrs();

	public Set<String> getAttrNames();

	public AttrType getAttrType(String attrName);

	public Object getAttrValue(String attrName);

	public void setAttrValue(String attrName, Object attrValue);

	public boolean hasAttr(String attrName);

	public String getId();

	public boolean hasNullId();

	public String getName();

	public void setName(String name);
}
