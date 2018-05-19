package es.ua.dlsi.im3.omr.encoding.agnostic.agnosticsymbols;

import es.ua.dlsi.im3.core.IM3Exception;
import es.ua.dlsi.im3.omr.encoding.agnostic.AgnosticSymbolType;

/**
 * @autor drizo
 */
public class Bracket extends AgnosticSymbolType {
    private static final String BRACKET = "bracket" + SEPSYMBOL;

    StartEnd startEnd;

    public Bracket(StartEnd startEnd) {
        this.startEnd = startEnd;
    }

    public Bracket() {

    }

    @Override
    public void setSubtype(String string) throws IM3Exception {
        startEnd = StartEnd.parseAgnosticString(string);
    }

    public StartEnd getStartEnd() {
        return startEnd;
    }

    public void setStartEnd(StartEnd startEnd) {
        this.startEnd = startEnd;
    }

    public static String getBracket() {
        return BRACKET;
    }

    @Override
    public String toAgnosticString() {
        return BRACKET + startEnd.toAgnosticString();
    }
}
