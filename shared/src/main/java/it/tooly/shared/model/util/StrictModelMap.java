/**
 *
 */
package it.tooly.shared.model.util;

import java.util.Collection;
import java.util.Iterator;

import it.tooly.shared.model.IModelObject;
import it.tooly.shared.model.attribute.IModelObjectAttribute;

/**
 * Extends the {@link ModelMap} but is strict in which objects are allowed to
 * put in. Only objects which share the same set of attributes can exist
 * together in this map. A hash will be calculated of the attributes of an
 * object that is put into the map. This hash will be compared with the hash of
 * the first object that was put in. The map will remember the first calculated
 * hash and this does not change when all entries are cleared.
 *
 * @author M.E. de Boer
 *
 */
public class StrictModelMap<T extends IModelObject> extends ModelMap<T> implements IStrictModelMap<T> {

	private static final long serialVersionUID = 8891592978180703001L;
	private int attrsHash = -1;
	private Class<T> objectType;

	public StrictModelMap(Class<T> objectType) {
		super();
		this.objectType = objectType;
	}

	public T put(T object) {
		if (this.attrsHash != -1 && !attributesHashMatches(object)) {
			throw new IllegalArgumentException("Object attributes hash doesn't match");
		}
		if (this.attrsHash == -1) {
			this.attrsHash = object.getAttrNames().hashCode();
		}
		return super.put(object);
	}

	public boolean attributesHashMatches(T object) {
		return (object.getAttrNames().hashCode() == this.attrsHash);
	}

	public Collection<IModelObjectAttribute<?>> getFirstObjectAttrs() {
		if (!this.isEmpty()) {
			Iterator<String> keysIt = this.keySet().iterator();
			T object = this.get(keysIt.next());
			return object.getAttrs();
		} else {
			return null;
		}
	}

	@Override
	public Class<T> getObjectType() {
		return this.objectType;
	}

}
