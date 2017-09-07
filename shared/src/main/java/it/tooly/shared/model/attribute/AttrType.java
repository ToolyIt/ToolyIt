package it.tooly.shared.model.attribute;

public enum AttrType {
	/**
	 * A String of characters
	 */
	STRING,
	/**
	 * An Integral may be a: Byte, Short, Integer, Long or BigInteger
	 */
	INTEGRAL("%d"),
	/**
	 * An Floating Point may be a: Float, Double or BigDecimal
	 */
	FLOATING_POINT("%f"), BOOLEAN("%b"), DATE("%tF"), DATETIME("%tF%tT"), OBJECTID, LOCKOWNER, CONTENTTYPE;

	private String textFormat;
	/**
	 * The value of this type of attribute needs to be of this class (or child
	 * of)
	 */
	private Class<? extends Object> valueClass;

	AttrType() {
		this.textFormat = "%s";
		this.valueClass = java.lang.String.class;
	}

	AttrType(String textFormat) {
		this.textFormat = textFormat;
	}

	public String getTextFormat() {
		return this.textFormat;
	}

	public Class<? extends Object> getValueClass() {
		return this.valueClass;
	}
}
