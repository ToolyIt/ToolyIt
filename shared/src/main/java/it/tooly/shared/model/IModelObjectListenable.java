package it.tooly.shared.model;

import java.beans.PropertyChangeListener;

import it.tooly.shared.model.attribute.IModelObjectAttribute;

public interface IModelObjectListenable extends IModelObject {

	public void addPropertyChangeListener(PropertyChangeListener listener);

	public void addPropertyChangeListener(IModelObjectAttribute<?> attribute, PropertyChangeListener listener);

	public void removePropertyChangeListener(PropertyChangeListener listener);

	public void removePropertyChangeListener(IModelObjectAttribute<?> attribute, PropertyChangeListener listener);
}
