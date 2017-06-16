package it.tooly.shared.model.util;

import java.util.Map;

import it.tooly.shared.common.ToolyException;
import it.tooly.shared.model.IModelObject;


public interface IModelMap<T extends IModelObject> extends Map<String, T> {
	/**
	 * When an object is put in this map its id ({@link IModelObject#getId()})
	 * will be used as the key of the map entry. If the map previously contained
	 * a mapping for the key, the old object is replaced.
	 *
	 * @see
	 *
	 * @param object
	 *            The object to put in the map.
	 * @return The object that was just put in the map.
	 * @throws ToolyException
	 *             when there is something wrong with the object
	 */
	public T put(T object) throws ToolyException;

	/**
	 * Get an object from the map by its id.
	 *
	 * @see java.util.Map#get(java.lang.Object)
	 */
	public T get(String id);
}
