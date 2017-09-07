package it.tooly.shared.model.util;

import java.util.Collection;

import it.tooly.shared.model.IModelObject;
import it.tooly.shared.model.attribute.IModelObjectAttribute;

public interface IStrictModelCollection<T extends IModelObject> {

	public Class<T> getObjectType();

	public boolean attributesHashMatches(T object);

	public Collection<IModelObjectAttribute<?>> getFirstObjectAttrs();
}
