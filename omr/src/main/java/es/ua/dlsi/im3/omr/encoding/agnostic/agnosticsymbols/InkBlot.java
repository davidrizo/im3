package es.ua.dlsi.im3.omr.encoding.agnostic.agnosticsymbols;

import es.ua.dlsi.im3.core.IM3Exception;
import es.ua.dlsi.im3.omr.encoding.agnostic.AgnosticSymbolType;

/**
 * Usually a blot ink
 * @autor drizo
 */
public class InkBlot extends AgnosticSymbolType {
    private static final String CODE = "inkBlot";

    public InkBlot() {
    }

    @Override
    public void setSubtype(String string) throws IM3Exception {

    }

    @Override
    public String toAgnosticString() {
        return CODE;
    }

}