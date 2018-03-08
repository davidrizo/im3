package es.ua.dlsi.im3.omr.encoding.agnostic.agnosticsymbols;

import es.ua.dlsi.im3.omr.encoding.agnostic.AgnosticSymbolType;

/**
 * An unknown symbol
 * @autor drizo
 */
public class Unknown extends AgnosticSymbolType {
    private static final String UNKNOWN = "unknown";

    @Override
    public String toAgnosticString() {
        return UNKNOWN;
    }

}
