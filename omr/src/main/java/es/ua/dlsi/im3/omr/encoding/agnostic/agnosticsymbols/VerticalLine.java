package es.ua.dlsi.im3.omr.encoding.agnostic.agnosticsymbols;

import es.ua.dlsi.im3.omr.encoding.agnostic.AgnosticSymbolType;

/**
 * @autor drizo
 */
public class VerticalLine extends AgnosticSymbolType {
    private static final String BARLINE = "barline";

    @Override
    public String toAgnosticString() {
        return BARLINE;
    }
}
