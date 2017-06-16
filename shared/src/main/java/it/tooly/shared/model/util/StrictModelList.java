/**
 *
 */
package it.tooly.shared.model.util;

import java.util.ArrayList;

import it.tooly.shared.model.IModelObject;

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
public class StrictModelList<T extends IModelObject> extends ArrayList<T> implements IModelList<T> {

	private static final long serialVersionUID = -8053195669557158204L;
	private int attrsHash = -1;

	public boolean add(T object) {
		if (this.attrsHash != -1 && !attributesHashMatches(object)) {
			throw new IllegalArgumentException("Object attributes hash doesn't match");
		}
		return super.add(object);
	}

	public boolean attributesHashMatches(T object) {
		return (object.getAttrNames().hashCode() == this.attrsHash);
	}

}
