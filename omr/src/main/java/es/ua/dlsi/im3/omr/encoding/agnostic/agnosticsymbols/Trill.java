package es.ua.dlsi.im3.omr.encoding.agnostic.agnosticsymbols;

import es.ua.dlsi.im3.core.IM3Exception;
import es.ua.dlsi.im3.omr.encoding.agnostic.AgnosticSymbolType;

/**
 * @autor drizo
 */
public class Trill extends AgnosticSymbolType {
    private static final String TRILL = "trill";

    public Trill() {
    }

    @Override
    public String toAgnosticString() {
        return TRILL;
    }

    @Override
    public void setSubtype(String string) throws IM3Exception {

    }

}
