package es.ua.dlsi.im3.omr.encoding.agnostic.agnosticsymbols;

import es.ua.dlsi.im3.core.IM3Exception;
import es.ua.dlsi.im3.omr.encoding.agnostic.AgnosticSymbolType;

/**
 * @autor drizo
 */
public class Dot extends AgnosticSymbolType {
    private static final String DOT = "dot";

    public Dot() {
    }

    @Override
    public String toAgnosticString() {
        return DOT;
    }

    @Override
    public void setSubtype(String string) throws IM3Exception {

    }
}
