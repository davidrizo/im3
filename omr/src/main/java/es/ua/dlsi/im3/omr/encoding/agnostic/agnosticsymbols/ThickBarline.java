package es.ua.dlsi.im3.omr.encoding.agnostic.agnosticsymbols;

import es.ua.dlsi.im3.omr.encoding.agnostic.AgnosticSymbolType;

/**
 * @autor drizo
 */
public class ThickBarline extends AgnosticSymbolType {
    private static final String THICKBARLINE = "thickbarline";

    @Override
    public String toAgnosticString() {
        return THICKBARLINE;
    }

}
