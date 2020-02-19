package es.ua.dlsi.im3.omr.encoding.agnostic.agnosticsymbols;

import es.ua.dlsi.im3.omr.encoding.agnostic.AgnosticSymbolType;

/**
 * @autor drizo
 */
public class Breath extends AgnosticSymbolType {
    private static final String BREATH = "breath";

    public Breath() {
    }

    @Override
    public String toAgnosticString() {
        return BREATH;
    }

    @Override
    public void setSubtype(String string) {
    }



}
