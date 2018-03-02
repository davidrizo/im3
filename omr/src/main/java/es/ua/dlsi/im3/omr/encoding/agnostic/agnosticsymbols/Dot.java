package es.ua.dlsi.im3.omr.encoding.agnostic.agnosticsymbols;

import es.ua.dlsi.im3.omr.encoding.agnostic.AgnosticSymbolType;

/**
 * @autor drizo
 */
public class Dot extends AgnosticSymbolType {
    private static final String DOT = "dot";

    @Override
    public String toAgnosticString() {
        return DOT;
    }
}
