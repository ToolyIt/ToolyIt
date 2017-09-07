package it.tooly.shared.model.attribute;

public class IdAttribute<T> extends ModelObjectAttribute<T> {

	public IdAttribute(String name, Class<T> clazz) {
		super(name, clazz, AttrType.OBJECTID);
	}

	public static <T> IdAttribute<T> create(String name, Class<T> clazz) {
		return new IdAttribute<T>(name, clazz);
	}

	public static IdAttribute<String> createStringId(String name) {
		return create(name, String.class);
	}

	public static IdAttribute<Long> createLongId(String name) {
		return create(name, Long.class);
	}

}
