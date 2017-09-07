package it.tooly.shared.model.attribute;

public interface IModelObjectAttribute<T> {

	/**
	 * @return the name of the attribute
	 */
	String getName();

	/**
	 * @return the type of the attribute
	 */
	AttrType getType();

	/**
	 * @return true if this attribute contains the id of another object. In most
	 *         cases this would be the same as {@link #getType()} ==
	 *         {@link AttrType#OBJECTID}
	 */
	boolean isObjectId();

	/**
	 * @return the type of value (its class)
	 */
	Class<T> getValueClass();

	boolean equals(IModelObjectAttribute<T> otherAttribute);

}