package it.tooly.dctmclient.model;

/**
 * An interface for a model object.
 * Each object should have an id (which can be read) and a name (which can be set and read).
 * 
 * @author M.E. de Boer
 *
 */
public interface IModelObject extends Comparable<IModelObject>  {
	/**
	 * Default id for the object if none has been specified
	 */
	public static final String NULL_ID = "OBJECT_WITHOUT_ID";
	/**
	 * @return true if this is an observable object. 
	 * In this case, the object should also implement {@link opensource.dctm.model.observable.IObservableObject} 
	 */
	public boolean isObservable();
	public String getId();
	public boolean hasNullId();
	public String getName();
	public void setName(String name);
}
