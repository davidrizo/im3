/*
 * Created on 13-ene-2004
 */
package es.ua.dlsi.im3.core;

/**
 * @author david
 */
public class IM3Exception extends Exception {
	/**
	 * Serial ID
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Constructor
	 *
	 * @param msg
	 */
	public IM3Exception(String msg) {
		super(msg);
	}

	public IM3Exception(Exception e) {
		super(e);
	}

	public IM3Exception(Throwable cause) {
		super(cause);
	}

	public IM3Exception(String message, Throwable cause) {
		super(message, cause);
	}

	@Override
	public String getMessage() {
		if (this.getCause() != null) {
			return super.getMessage() + '\n' + this.getCause().getMessage();
		} else {
			return super.getMessage();
		}
	}
}
