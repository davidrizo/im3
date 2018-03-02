package es.ua.dlsi.im3.omr.encoding.agnostic.agnosticsymbols;

import es.ua.dlsi.im3.omr.encoding.agnostic.AgnosticSymbolType;

/**
 * @autor drizo
 */
public class Multirest extends AgnosticSymbolType {
    private static final String MR = "multirest";

    @Override
    public String toAgnosticString() {
        return MR;
    }
}
