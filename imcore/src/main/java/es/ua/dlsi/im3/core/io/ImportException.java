/*
 * Created on 10-ene-2004
 */
package es.ua.dlsi.im3.core.io;

/**
 * Raised in the process of import a song
 *
 * @author david
 */
public class ImportException extends Exception {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    /**
     * Constructor from another exception
     *
     * @param e Source exception
     */
    public ImportException(Exception e) {
	super(e);
    }

    /**
     * Constructor from a message
     *
     * @param msg
     */
    public ImportException(String msg) {
	super(msg);
    }

    public ImportException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
	super(message, cause, enableSuppression, writableStackTrace);
    }

    public ImportException(String message, Throwable cause) {
	super(message, cause);
    }
    
    

}
