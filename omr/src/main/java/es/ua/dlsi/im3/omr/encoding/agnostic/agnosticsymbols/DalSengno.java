package es.ua.dlsi.im3.omr.encoding.agnostic.agnosticsymbols;

import es.ua.dlsi.im3.omr.encoding.agnostic.AgnosticSymbolType;

/**
 * @autor drizo
 */
public class DalSengno extends AgnosticSymbolType {
    private static final String DS = "dalSegno";

    @Override
    public String toAgnosticString() {
        return DS;
    }

}
