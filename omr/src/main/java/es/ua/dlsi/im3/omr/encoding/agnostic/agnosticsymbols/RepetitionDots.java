package es.ua.dlsi.im3.omr.encoding.agnostic.agnosticsymbols;

import es.ua.dlsi.im3.core.IM3Exception;
import es.ua.dlsi.im3.omr.encoding.agnostic.AgnosticSymbolType;

/**
 * @autor drizo
 */
public class RepetitionDots extends AgnosticSymbolType {
    private static final String CODE = "repetitionDots";

    public RepetitionDots() {
    }

    @Override
    public void setSubtype(String string) {

    }

    @Override
    public String toAgnosticString() {
        return CODE;
    }

}
