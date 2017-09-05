/*
 * Created on 22-ene-2004
 */
package es.ua.dlsi.im3.core.adt.tree;

/**
 * @author david
 */
public class ExportTreeException extends Exception {
	/**
	 * 
	 */
	private static final long serialVersionUID = 8444058308285052195L;

	/**
	 * @param string
	 */
	public ExportTreeException(String string) {
		super(string);
	}

	public ExportTreeException(Exception e) {
		super(e);
	}
}
