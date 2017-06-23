package it.tooly.shared.model;

import org.apache.commons.lang.StringUtils;

import it.tooly.shared.common.ToolyException;

public class ModelObjectAttribute<T extends Object> {

	private String name;
	private AttrType type;
	private Class<T> typeClass;
	private T value;

	public static enum AttrType {
		STRING(false), INTEGER(false), DOUBLE(false), BOOLEAN(false), DATE(false), ID(true), LOCK_OWNER(
				true), CONTENT_TYPE(true);
		private boolean custom;

		AttrType(boolean custom) {
			this.custom = custom;
		}

		public boolean isCustom() {
			return this.custom;
		}

		public Class<? extends Object> getTypeClass() {
			if (this.custom) {
				return Object.class;
			}
			String className = StringUtils.capitalize(this.toString().toLowerCase());
			try {
				return Class.forName("java.lang." + className);
			} catch (ClassNotFoundException e) {
				// System.out.println(e.getMessage());
			}
			try {
				return Class.forName("java.util." + className);
			} catch (ClassNotFoundException e) {
				// System.out.println(e.getMessage());
			}
			return null;
		}
	}

	@SuppressWarnings("unchecked")
	public ModelObjectAttribute(String name, Object value, AttrType type) throws ToolyException {
		this.name = name;
		this.type = type;
		try {
			this.typeClass = (Class<T>) type.getTypeClass();
		} catch (ClassCastException e) {
			throw new ToolyException(
					"Can't construct this attribute because the given type class doesn't match the generic class", e);
		}
		this.value = (T) value;
	}

	public ModelObjectAttribute(String name, T value, Class<T> typeClass) throws ToolyException {
		this.type = AttrType.valueOf(typeClass.getSimpleName().toUpperCase());
		if (this.type == null)
			throw new ToolyException(
					"Can't construct this attribute because there is not known AttrType of the given type class");
		this.name = name;
		this.value = value;
		this.typeClass = typeClass;
	}

	public ModelObjectAttribute(String name, T value, AttrType type, Class<T> typeClass) throws ToolyException {
		if (typeClass != null && !type.getTypeClass().isAssignableFrom(typeClass)) {
			throw new ToolyException(
					"Can't construct this attribute because the class of the given type class doesn't the given generic class");
		}
		this.name = name;
		this.value = value;
		this.type = type;
		this.typeClass = typeClass;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name
	 *            the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the type
	 */
	public AttrType getType() {
		return type;
	}

	/**
	 * @param type
	 *            the type to set
	 */
	public void setType(AttrType type) {
		this.type = type;
	}

	/**
	 * @return the typeClass
	 */
	public Class<T> getTypeClass() {
		return typeClass;
	}

	/**
	 * @return the typeClass
	 */
	public void setTypeClass(Class<T> typeClass) {
		this.typeClass = typeClass;
	}

	/**
	 * @return the value
	 */
	public T getValue() {
		return value;
	}

	/**
	 * @param value
	 *            the value to set
	 */
	@SuppressWarnings("unchecked")
	public void setValue(Object value) {
		this.value = (T) value;
	}

	public String toString() {
		return this.name + " " + this.type + " = '" + this.value + "'";
	}
}
