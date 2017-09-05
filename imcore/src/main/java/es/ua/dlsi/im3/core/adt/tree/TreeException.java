/*
 * Created on 10-ene-2004
 */
package es.ua.dlsi.im3.core.adt.tree;

/**
 * Exceptions thrown by tree operations
 * @author david
 */
public class TreeException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8256958817128296998L;

	/** 
	 * Constructor from a message
	 * @param msg
	 */
	public TreeException(String msg) {
		super(msg);
	}

}
