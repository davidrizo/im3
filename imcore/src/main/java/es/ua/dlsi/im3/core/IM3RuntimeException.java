package es.ua.dlsi.im3.core;

/**
 *
 * @author drizo
 */
public class IM3RuntimeException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5986252837490028393L;

	public IM3RuntimeException() {
	}

	public IM3RuntimeException(String message, Throwable cause) {
		super(message, cause);
	}

	public IM3RuntimeException(String message) {
		super(message);
	}

	public IM3RuntimeException(Throwable cause) {
		super(cause);
	}

}
