package es.ua.dlsi.im3.omr.encoding.agnostic.agnosticsymbols;

import es.ua.dlsi.im3.omr.encoding.agnostic.AgnosticSymbolType;

/**
 * @autor drizo
 */
public class Slur extends AgnosticSymbolType {
    private static final String SLUR = "slur" + SEPSYMBOL;

    StartEnd startEnd;

    public Slur(StartEnd startEnd) {
        this.startEnd = startEnd;
    }

    public Slur() {

    }

    public StartEnd getStartEnd() {
        return startEnd;
    }

    public void setStartEnd(StartEnd startEnd) {
        this.startEnd = startEnd;
    }

    public static String getSLUR() {
        return SLUR;
    }

    @Override
    public String toAgnosticString() {
        return SLUR + startEnd.toAgnosticString();
    }
}
