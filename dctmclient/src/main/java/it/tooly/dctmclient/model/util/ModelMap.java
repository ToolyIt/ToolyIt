package it.tooly.dctmclient.model.util;

import java.util.HashMap;

import it.tooly.dctmclient.model.IModelObject;


public class ModelMap<T extends IModelObject> extends HashMap<String, T> implements IModelMap<T> {

	private static final long serialVersionUID = -2809501448118752674L;

	public T put(T object) {
		return this.put(object.getId(), object);
	}
}
