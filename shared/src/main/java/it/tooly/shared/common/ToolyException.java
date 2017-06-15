package it.tooly.shared.common;

public class ToolyException extends Exception {

	private static final long serialVersionUID = -5550922407659369856L;

	public ToolyException(String message) {
		super(message);
	}

	public ToolyException(Throwable cause) {
		super(cause);
	}

	public ToolyException(String message, Throwable cause) {
		super(message, cause);
	}

}
