package it.tooly.shared.model.attribute;

public class ModelObjectAttribute<T> implements IModelObjectAttribute<T> {
	private final Class<T> clazz;
	private String name;
	private AttrType type;

	public ModelObjectAttribute(String name, AttrType type) {
		this(name, null, type);
	}

	public ModelObjectAttribute(String name, Class<T> clazz, AttrType type) {
		this.name = name;
		this.type = type;
		this.clazz = clazz;
	}

	public static <T> ModelObjectAttribute<T> create(String name, Class<T> clazz, AttrType type) {
		return new ModelObjectAttribute<T>(name, clazz, type);
	}

	public static ModelObjectAttribute<String> createStringAttr(String name) {
		return create(name, String.class, AttrType.STRING);
	}

	/* (non-Javadoc)
	 * @see it.tooly.shared.model.attribute.IModelObjectAttribute#getName()
	 */
	@Override
	public String getName() {
		return name;
	}

	/* (non-Javadoc)
	 * @see it.tooly.shared.model.attribute.IModelObjectAttribute#setName(java.lang.String)
	 */
	public void setName(String name) {
		this.name = name;
	}

	/* (non-Javadoc)
	 * @see it.tooly.shared.model.attribute.IModelObjectAttribute#getType()
	 */
	@Override
	public AttrType getType() {
		return type;
	}

	/* (non-Javadoc)
	 * @see it.tooly.shared.model.attribute.IModelObjectAttribute#setType(it.tooly.shared.model.attribute.AttrType)
	 */
	public void setType(AttrType type) {
		this.type = type;
	}

	public String toString() {
		return this.type + " " + this.name;
	}

	public boolean equals(IModelObjectAttribute<T> otherAttribute) {
		return this.name.equals(otherAttribute.getName()) && this.type == otherAttribute.getType();
	}

	@Override
	public Class<T> getValueClass() {
		return this.clazz;
	}

	@Override
	public boolean isObjectId() {
		return (this.type == AttrType.OBJECTID);
	}
}
