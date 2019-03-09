package es.ua.dlsi.im3.omr.encoding.agnostic.agnosticsymbols;

import es.ua.dlsi.im3.omr.encoding.agnostic.AgnosticSymbolType;

/**
 * @autor drizo
 */
public class Colon extends AgnosticSymbolType {
    private static final String COLON = "colon";

    public Colon() {
    }

    @Override
    public String toAgnosticString() {
        return COLON;
    }

    @Override
    public void setSubtype(String string) {

    }
}
