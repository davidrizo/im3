package es.ua.dlsi.im3.omr.encoding.agnostic.agnosticsymbols;

import es.ua.dlsi.im3.core.IM3Exception;
import es.ua.dlsi.im3.omr.encoding.agnostic.AgnosticSymbolType;

/**
 * @autor drizo
 */
public class Multirest extends AgnosticSymbolType {
    private static final String MR = "multirest";

    public Multirest() {
    }

    @Override
    public void setSubtype(String string) {

    }

    @Override
    public String toAgnosticString() {
        return MR;
    }
}
