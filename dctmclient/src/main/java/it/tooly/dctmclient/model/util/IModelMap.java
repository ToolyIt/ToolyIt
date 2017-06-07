package it.tooly.dctmclient.model.util;

import java.util.Map;

import it.tooly.dctmclient.model.IModelObject;


public interface IModelMap<T extends IModelObject> extends Map<String, T> {
	public T put(T object);
	public T get(Object key);
}
