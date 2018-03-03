package es.ua.dlsi.im3.omr.encoding.agnostic.agnosticsymbols;

import es.ua.dlsi.im3.omr.encoding.agnostic.AgnosticSymbolType;

/**
 * @autor drizo
 */
public class Trill extends AgnosticSymbolType {
    private static final String TRILL = "trill";

    @Override
    public String toAgnosticString() {
        return TRILL;
    }

}