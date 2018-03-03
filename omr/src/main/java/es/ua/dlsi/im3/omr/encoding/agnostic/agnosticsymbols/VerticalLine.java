package es.ua.dlsi.im3.omr.encoding.agnostic.agnosticsymbols;

import es.ua.dlsi.im3.omr.encoding.agnostic.AgnosticSymbolType;

/**
 * @autor drizo
 */
public class VerticalLine extends AgnosticSymbolType {
    //private static final String BARLINE = "barline"; // Primus v.1
    private static final String BARLINE = "verticalLine"; // Primus v.1

    @Override
    public String toAgnosticString() {
        return BARLINE;
    }
}
