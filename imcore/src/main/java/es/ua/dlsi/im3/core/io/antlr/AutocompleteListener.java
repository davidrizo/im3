package es.ua.dlsi.im3.core.io.antlr;

import java.util.ArrayList;
import java.util.List;

import org.antlr.v4.runtime.BaseErrorListener;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.Recognizer;
import org.antlr.v4.runtime.misc.Interval;
import org.antlr.v4.runtime.misc.IntervalSet;

/**
 *
 * @author drizo
 */
public class AutocompleteListener extends BaseErrorListener {
    ArrayList<Integer> expectedTokens; //TODO ¿Para cada elemento?
    
    public AutocompleteListener() {
	expectedTokens = new ArrayList<>();
    }
    
    @Override
    public void syntaxError(Recognizer<?, ?> recognizer, Object offendingSymbol, int line, int charPositionInLine, String msg, RecognitionException e) {
	super.syntaxError(recognizer, offendingSymbol, line, charPositionInLine, msg, e);
	IntervalSet expectedItems = e.getExpectedTokens();
	/*Token token = e.getOffendingToken();
	System.out.println("Token: " + token.getText() + " at " + (token.getCharPositionInLine()+1));

	for (int i=0; i<recognizer.getTokenNames().length; i++) {
	    System.out.println("i=" +i + " > " + recognizer.getTokenNames()[i]);
	}*/
	System.out.println("MSG=" + msg);
	if (expectedItems != null && expectedItems.getIntervals() != null) {
	    for (Interval interval: expectedItems.getIntervals()) {
		for (int j=interval.a; j<=interval.b; j++) {
		    expectedTokens.add(j);
		}
	    }
	} else {
	    expectedTokens = null;
	}
		
    }

    public List<Integer> getExpectedTokens() {
	return expectedTokens;
    }
}
