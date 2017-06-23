package it.tooly.shared.model.util;

import java.util.Set;

import it.tooly.shared.model.IModelObject;
import it.tooly.shared.model.ModelObjectAttribute;

public interface IStrictModelCollection<T extends IModelObject> {

	public boolean attributesHashMatches(T object);

	public Set<ModelObjectAttribute<?>> getFirstObjectAttrs();
}
