package es.ua.dlsi.im3.core.io.antlr;

import es.ua.dlsi.im3.core.io.ImportException;
import java.util.ArrayList;

/**
 *
 * @author drizo
 */
public class GrammarParseException extends ImportException {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	ArrayList<ParseError> errors;

    public GrammarParseException(String errorMessage, ArrayList<ParseError> errors) {
		super(errorMessage);
		this.errors = errors;
    }
    
    public ArrayList<ParseError> getErrors() {
	return errors;
    }
    
}
