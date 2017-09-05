/*
 * Created on 10-ene-2004
 */
package es.ua.dlsi.im3.core.io;

/**
 * Raised in the process of import a song
 * @author david
 */
public class ExportException extends Exception {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Constructor from another exception
	 * @param e Source exception
	 */
	public ExportException(Exception e) {
		super(e);
	}

	/**
	 * Constructor from a message
	 * @param msg
	 */
	public ExportException(String msg) {
		super(msg);
	}

}
