package it.tooly.shared.model.util;

import java.util.LinkedHashMap;

import org.apache.commons.lang.StringUtils;

import it.tooly.shared.model.IModelObject;

/**
 * @author M.E. de Boer
 *
 * @param <T>
 *            The type of objects which will be put in this map. Because these
 *            objects implement {@link IModelObject} they will have the getId
 *            method and this will be used to define the key of the object entry
 *            in the map.
 */
public class ModelMap<T extends IModelObject> extends LinkedHashMap<String, T> implements IModelMap<T> {

	private static final long serialVersionUID = -2809501448118752674L;

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * it.tooly.dctmclient.model.util.IModelMap#put(it.tooly.dctmclient.model.
	 * IModelObject)
	 */
	public T put(T object) {
		if (StringUtils.isBlank(object.getId()))
			throw new IllegalArgumentException("Object key is blank");
		return super.put(object.getId(), object);
	}

	@Override
	public T get(String id) {
		return super.get(id);
	}
}
