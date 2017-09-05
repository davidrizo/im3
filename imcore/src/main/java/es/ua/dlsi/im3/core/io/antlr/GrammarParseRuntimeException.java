package es.ua.dlsi.im3.core.io.antlr;

/**
 *
 * @author drizo
 */
public class GrammarParseRuntimeException extends RuntimeException {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public GrammarParseRuntimeException(String message) {
	super(message);
    }

    public GrammarParseRuntimeException(Throwable cause) {
	super(cause);
    }
}
