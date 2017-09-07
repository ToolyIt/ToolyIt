/**
 *
 */
package it.tooly.shared.model.util;

import java.util.ArrayList;
import java.util.Collection;

import it.tooly.shared.model.IModelObject;
import it.tooly.shared.model.attribute.IModelObjectAttribute;

/**
 * Extends the {@link ArrayList} but is strict in which objects are allowed to
 * be added. Only objects which share the same set of attributes can exist
 * together in this list. A hash will be calculated of the attributes of an
 * object that is added to the list. This hash will be compared with the hash of
 * the first object that was added. The map will remember the first calculated
 * hash and this does not change when all entries are cleared.
 *
 * @author M.E. de Boer
 *
 */
public class StrictModelList<T extends IModelObject> extends ArrayList<T>
		implements IModelList<T>, IStrictModelList<T> {

	private static final long serialVersionUID = -8053195669557158204L;
	private final Class<T> objectType;
	private int attrsHash = -1;

	public StrictModelList(Class<T> objectType) {
		super();
		this.objectType = objectType;
	}

	public boolean add(T object) {
		if (this.attrsHash != -1 && !attributesHashMatches(object)) {
			throw new IllegalArgumentException("Object attributes hash doesn't match");
		}
		if (this.attrsHash == -1) {
			this.attrsHash = object.getAttrNames().hashCode();
		}
		return super.add(object);
	}

	public boolean attributesHashMatches(T object) {
		return (object.getAttrNames().hashCode() == this.attrsHash);
	}

	public Class<T> getObjectType() {
		return this.objectType;
	}

	public Collection<IModelObjectAttribute<?>> getFirstObjectAttrs() {
		if (!this.isEmpty()) {
			return this.get(0).getAttrs();
		} else {
			return null;
		}
	}


}
